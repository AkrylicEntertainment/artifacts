package dev.nateweisz.bytestore.user.controller

import dev.nateweisz.bytestore.user.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(val userService: UserService) {
}