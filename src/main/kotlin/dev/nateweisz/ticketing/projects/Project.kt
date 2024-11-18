package dev.nateweisz.ticketing.projects

import dev.nateweisz.ticketing.user.User
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "projects")
data class Project(
    @Id var id: String, // version of name except with spaces replaces with -'s
    var name: String,

    // The member's emails
    val members: MutableList<String> = mutableListOf(),

    // metadata
    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)