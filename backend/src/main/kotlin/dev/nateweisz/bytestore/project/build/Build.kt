package dev.nateweisz.bytestore.project.build

import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "builds")
data class Build(
    val projectId: Long,
    val commitHash: String,
    val buildAt: LocalDateTime = LocalDateTime.now(),
    var location: BuildLocation = BuildLocation.LOCAL,

    // TODO: checksums
)