package dev.nateweisz.bytestore.user

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.UUID

@Document(collection = "users")
data class User(
    @Id val id: UUID,
    val email: String,
    val username: String,
    val picture: String,

    val joinedAt: Instant = Instant.now(),

    // Github Login Info

)