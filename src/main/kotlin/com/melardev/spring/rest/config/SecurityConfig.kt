package com.melardev.spring.rest.config

import com.melardev.spring.rest.services.AppUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import java.util.*

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig {

    private var passwordEncoder: DelegatingPasswordEncoder? = null

    @Bean
    fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
                .csrf().disable()
                .authorizeExchange()
                // Allow anything, restriction will be enforce through anotations
                .pathMatchers("/**").permitAll()
                // .anyExchange().authenticated()
                .and()
                .httpBasic()
                .and()
                .formLogin().disable()
                .build()
    }

    @Suppress("deprecation")
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        if (passwordEncoder == null) {
            val encodingId = "bcrypt"
            val encoders = HashMap<String, PasswordEncoder>()
            encoders[encodingId] = BCryptPasswordEncoder()
            encoders["ldap"] = org.springframework.security.crypto.password.LdapShaPasswordEncoder()
            encoders["MD4"] = org.springframework.security.crypto.password.Md4PasswordEncoder()
            encoders["MD5"] = org.springframework.security.crypto.password.MessageDigestPasswordEncoder("MD5")
            encoders["noop"] = org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance()
            encoders["pbkdf2"] = Pbkdf2PasswordEncoder()
            encoders["scrypt"] = SCryptPasswordEncoder()
            encoders["SHA-1"] = org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-1")
            encoders["SHA-256"] = org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-256")
            encoders["sha256"] = org.springframework.security.crypto.password.StandardPasswordEncoder()

            passwordEncoder = DelegatingPasswordEncoder(encodingId, encoders)
            passwordEncoder!!.setDefaultPasswordEncoderForMatches(BCryptPasswordEncoder(10))
        }
        return passwordEncoder!!
    }

    @Bean
    fun inMemoryUserDetailsService(passwordEncoder: PasswordEncoder): MapReactiveUserDetailsService {
        // The prefix {noop} is there because I use the new DelegatingPasswordEncoder

        val password = passwordEncoder.encode("user")
        // password = "{noop}user";
        val user = User.builder()
                .username("user")
                .password(password)
                .roles("USER")
                .build()

        return MapReactiveUserDetailsService(user)
    }

    @Bean
    @Primary
    fun dbUserDetailsService(): ReactiveUserDetailsService {
        return AppUserDetailsService()
    }
}
