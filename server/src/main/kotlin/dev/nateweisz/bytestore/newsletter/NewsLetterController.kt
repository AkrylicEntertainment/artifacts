package dev.nateweisz.bytestore.newsletter

import dev.nateweisz.bytestore.annotations.RateLimited
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/newsletter")
class NewsLetterController(private val newsLetterRepository: NewsLetterRepository) {
    @PostMapping("/signup")
    @RateLimited(1)
    fun signUp(@RequestBody email: String) {
        newsLetterRepository.save(NewsLetterSignUp(email))
    }
}