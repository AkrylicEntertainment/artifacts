package dev.nateweisz.bytestore.project.build

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.UUID

@Document(collection = "builds")
data class Build(
    @Id
    val id: UUID = UUID.randomUUID(),
    val projectId: Long,
    val commitHash: String,
    val builtAt: LocalDateTime = LocalDateTime.now(),
    var location: BuildLocation = BuildLocation.LOCAL,
    var status: BuildStatus

    // TODO: checksums
)