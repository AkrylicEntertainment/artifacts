package dev.nateweisz.bytestore.project

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "projects")
data class Project(
    // the github repository id
    @Id
    val id: String,

    val userId: Long,
    val username: String, // we should make a scheduler that updates them every 24 hours
    // these requests need to be spread out

    val repoName: String,

    val buildsRun: Int,
    val downloads: Long
)