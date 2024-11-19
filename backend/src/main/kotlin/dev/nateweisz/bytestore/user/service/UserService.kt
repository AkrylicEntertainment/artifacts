package dev.nateweisz.bytestore.user.service

import dev.nateweisz.bytestore.user.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

const val GITHUB_URL = "https://github.com/"
const val GITHUB_OAUTH_URL = "$GITHUB_URL/login/oauth/authorize?scope=user:email?client_id=%client_id%"

@Service
class UserService(val userRepository: UserRepository) {

    @Value("\${github.client-id}")
    lateinit var clientId: String

    @Value("\${github.client-secret}")
    lateinit var clientSecret: String

    val oAuthUrl get() = GITHUB_OAUTH_URL.replace("%client_id%", clientId)
}