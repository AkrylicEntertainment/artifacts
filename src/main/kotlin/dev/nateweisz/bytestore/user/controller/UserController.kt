package dev.nateweisz.bytestore.user.controller

import dev.nateweisz.bytestore.user.User
import dev.nateweisz.bytestore.user.UserRepository
import dev.nateweisz.bytestore.user.service.UserService
import jakarta.servlet.http.HttpSession
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user/")
class UserController(val userRepository: UserRepository) {

    @GetMapping("/me")
    fun me(session: HttpSession): ResponseEntity<User?> {
        return (session.getAttribute("user_id") as? String)?.let { ResponseEntity.ok(userRepository.findByGithubId(it)) } ?: ResponseEntity.status(501).build()
    }
}