package dev.nateweisz.bytestore.user.service

import dev.nateweisz.bytestore.user.User
import dev.nateweisz.bytestore.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserService(val userRepository: UserRepository, val clientService: OAuth2AuthorizedClientService) {

    fun processOAuthPostLogin(authentication: OAuth2AuthenticationToken): User {
        val oauth2User = authentication.principal


        val githubId = oauth2User.name
        val username = oauth2User.getAttribute<String>("login")!!
        val email = oauth2User.getAttribute<String>("email")!!
        val avatarUrl = oauth2User.getAttribute<String>("avatar_url")!!

        var existingUser: User? = userRepository.findByGithubId(githubId)

        if (existingUser == null) {
            existingUser = User(
                id = "",
                githubId = githubId,
                username = username,
                email = email,
                avatarUrl = avatarUrl,
                firstName = "",
                lastName = "",
                lastLogin = LocalDateTime.now()
            )

        }

        existingUser.lastLogin = LocalDateTime.now()

        return userRepository.save(existingUser)
    }
}
