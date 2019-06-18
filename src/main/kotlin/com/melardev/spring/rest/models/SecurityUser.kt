package com.melardev.spring.rest.models

import com.melardev.spring.rest.entities.Role
import com.melardev.spring.rest.entities.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

class SecurityUser(userDb: User) : UserDetails {

    private val username: String? = userDb.username
    private val password: String? = userDb.password
    private val roles: Set<Role>? = userDb.roles

    init {
        // For a complex app you would take the roles, locked, credential expired
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        val roleIterator = roles?.iterator()
        val roles = ArrayList<GrantedAuthority>()
        if (roleIterator != null) {
            while (roleIterator.hasNext()) {
                roles.add(SimpleGrantedAuthority(roleIterator.next().name))
            }
        }
        return roles
    }

    override fun getPassword(): String? {
        return password
    }

    override fun getUsername(): String? {
        return username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

}
