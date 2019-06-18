package com.melardev.spring.rest.repositories

import com.melardev.spring.rest.entities.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface UserRepository : ReactiveCrudRepository<User, String> {
    fun findByUsername(username: String): Mono<User>
}
