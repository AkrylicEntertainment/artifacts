package dev.nateweisz.bytestore.user.service

import dev.nateweisz.bytestore.user.User
import dev.nateweisz.bytestore.user.UserRepository
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserService(val userRepository: UserRepository, val oAuthClientService: OAuth2AuthorizedClientService) {

    fun processOAuthPostLogin(oAuth2User: OAuth2User): User {
        val githubId = oAuth2User.getAttribute<Int>("id").toString()
        val username = oAuth2User.getAttribute<String>("login")!!
        val avatarUrl = oAuth2User.getAttribute<String>("avatar_url")!!
        val display = oAuth2User.getAttribute<String>("name")!!

        var existingUser: User? = userRepository.findByGithubId(githubId)
        if (existingUser == null) {
            existingUser = User(
                githubId = githubId,
                username = username,
                avatarUrl = avatarUrl,
                lastLogin = LocalDateTime.now(),
                display = display
            )
        }

        existingUser.lastLogin = LocalDateTime.now()
        return userRepository.save(existingUser)
    }
}
