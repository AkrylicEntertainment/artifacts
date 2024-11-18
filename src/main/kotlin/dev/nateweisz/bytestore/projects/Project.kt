package dev.nateweisz.ticketing.projects

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "projects")
data class Project(
    @Id var id: String, // version of name except with spaces replaces with -'s
    var name: String,
    var visibility: Visibility = Visibility.PUBLIC,

    // The member's emails
    val members: MutableList<String> = mutableListOf(),

    // metadata
    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
) {
}