package dev.nateweisz.bytestore.project.build

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.UUID

interface BuildRepository : MongoRepository<Build, UUID> {
    fun findByProjectIdAndCommitHashIn(projectId: Long, commitHashes: List<String>): List<Build>
}