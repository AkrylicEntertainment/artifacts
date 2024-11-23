package dev.nateweisz.bytestore.newsletter

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "newsletter")
data class NewsLetterSignUp(
    @Id
    val email: String,
    val signedUpAt: LocalDateTime = LocalDateTime.now()
) {
}