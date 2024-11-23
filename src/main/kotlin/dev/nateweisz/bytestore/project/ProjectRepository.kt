package dev.nateweisz.bytestore.project

import org.springframework.data.mongodb.repository.MongoRepository

interface ProjectRepository : MongoRepository<Project, Long> {
    fun findTop10ByOrderByDownloadsDesc(): List<Project>
    fun findByUsernameAndRepoName(username: String, repoName: String): Project?
    fun findAllByUserId(userId: Long): List<Project>
}
