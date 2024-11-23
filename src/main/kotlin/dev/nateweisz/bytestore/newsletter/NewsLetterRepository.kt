package dev.nateweisz.bytestore.newsletter

import org.springframework.data.mongodb.repository.MongoRepository

interface NewsLetterRepository : MongoRepository<NewsLetterSignUp, String> {
}