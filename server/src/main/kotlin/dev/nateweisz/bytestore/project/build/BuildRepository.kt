package dev.nateweisz.bytestore.project.build

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.UUID

interface BuildRepository : MongoRepository<Build, UUID> {
    fun findByProjectIdAndCommitHash(projectId: Long, commitHash: String): Build?
    fun findByProjectIdAndCommitHashIn(projectId: Long, commitHashes: List<String>): List<Build>
}