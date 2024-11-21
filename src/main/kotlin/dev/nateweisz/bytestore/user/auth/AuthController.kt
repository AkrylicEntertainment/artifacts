package dev.nateweisz.bytestore.user.auth

import dev.nateweisz.bytestore.user.User
import dev.nateweisz.bytestore.user.service.UserService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.io.IOException


@RestController
class AuthController(val userService: UserService) {

    @GetMapping("/login/success")
    fun loginSuccess(authentication: OAuth2AuthenticationToken?, response: HttpServletResponse) {
        val user: User = userService.processOAuthPostLogin(authentication!!)
        response.sendRedirect("http://localhost:3000/dashboard?userId")
    }
}