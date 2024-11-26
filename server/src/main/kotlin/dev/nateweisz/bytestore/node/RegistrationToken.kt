package dev.nateweisz.bytestore.node

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "registration_tokens")
data class RegistrationToken(
    @Id
    val token: String,
    val expiration: Long
) {
    fun isExpired(): Boolean {
        return System.currentTimeMillis() > expiration
    }
}