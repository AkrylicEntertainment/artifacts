package dev.nateweisz.bytestore.node.repository

import dev.nateweisz.bytestore.node.Node
import org.springframework.data.mongodb.repository.MongoRepository

interface NodeRepository : MongoRepository<Node, String> {
}