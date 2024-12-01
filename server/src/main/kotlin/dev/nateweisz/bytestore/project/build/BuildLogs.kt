package dev.nateweisz.bytestore.project.build

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document(collection = "build_logs")
data class BuildLogs(
    @Id
    val buildId: UUID,
    val logs: String
)
