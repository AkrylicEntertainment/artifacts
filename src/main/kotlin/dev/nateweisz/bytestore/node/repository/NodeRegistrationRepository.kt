package dev.nateweisz.bytestore.node.repository

import dev.nateweisz.bytestore.node.RegistrationToken
import org.springframework.data.mongodb.repository.MongoRepository

interface NodeRegistrationRepository : MongoRepository<RegistrationToken, String> {}