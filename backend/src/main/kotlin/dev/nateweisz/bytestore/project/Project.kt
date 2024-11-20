package dev.nateweisz.bytestore.project

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "projects")
data class Project(
    @Id
    val id: Long,
) {
}