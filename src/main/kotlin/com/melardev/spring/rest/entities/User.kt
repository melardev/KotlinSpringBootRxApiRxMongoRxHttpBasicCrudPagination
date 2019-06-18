package com.melardev.spring.rest.entities

import org.springframework.data.mongodb.core.mapping.Document

@Document
data class User(var username: String? = null,
                var password: String? = null,
                var roles: MutableSet<Role>? = mutableSetOf()) : TimeStampedDocument() {
    fun addRole(role: Role) {
        this.roles?.add(role)
    }
}
