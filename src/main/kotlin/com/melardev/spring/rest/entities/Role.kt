package com.melardev.spring.rest.entities

import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Role(var name: String) : TimeStampedDocument() {

    override fun equals(obj: Any?): Boolean {
        return obj!!.javaClass == Role::class.java && (obj as Role).name.equals(name, ignoreCase = true)
    }
}
