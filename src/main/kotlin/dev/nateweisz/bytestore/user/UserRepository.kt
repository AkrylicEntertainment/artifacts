package dev.nateweisz.bytestore.user

import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<User, String> {
    fun findByGithubId(githubId: String): User?
}