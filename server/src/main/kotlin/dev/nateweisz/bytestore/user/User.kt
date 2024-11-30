package dev.nateweisz.bytestore.user

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime


@Document(collection = "users")
class User(
    @Id
    val githubId: String,
    val username: String,
    val avatarUrl: String,

    val display: String,

    val createdAt: LocalDateTime = LocalDateTime.now(),
    var lastLogin: LocalDateTime,
    val admin: Boolean = false
) {}