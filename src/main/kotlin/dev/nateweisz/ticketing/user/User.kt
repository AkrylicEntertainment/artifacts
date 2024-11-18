package dev.nateweisz.ticketing.user

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document(collection = "users")
data class User(
    @Id val id: UUID,
    val oAuthProvider: OAuthProvider
)
