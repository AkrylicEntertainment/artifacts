package dev.nateweisz.bytestore.user.controller

import dev.nateweisz.bytestore.user.data.GithubCallbackRequest
import dev.nateweisz.bytestore.user.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(val userService: UserService) {

    /*
     * AUTH FLOW DESCRIBED BELOW
     * 1. Frontend sends user to {BASE_URL}/api/auth/github
     * 2. We redirect them to GitHub login page
     * 3. Github login page redirects them to the frontend with a code and state
     * 4. Frontend sends POST to {BASE_URL}/api/auth/github/callback with the state and code provided
     * 5. Session Token is returned to frontend
     */
    @GetMapping("/auth/github")
    fun githubAuth() = "redirect:${userService.oAuthUrl}"

    @PostMapping("/auth/github/callback")
    fun githubCallback(request: GithubCallbackRequest) {

    }
}