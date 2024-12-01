package dev.nateweisz.bytestore.node.websocket.s2n

import dev.nateweisz.bytestore.node.websocket.writeString
import java.nio.ByteBuffer

class RequestBuildMessage(val owner: String, val repository: String, val commitHash: String, val secret: String) : S2NProtocolMessage {
    override fun write(): ByteArray {
        val buffer = ByteBuffer.allocate(4 + owner.length + 4 + repository.length + 4 + commitHash.length + 4 + secret.length)
        buffer.writeString(owner)
        buffer.writeString(repository)
        buffer.writeString(commitHash)
        buffer.writeString(secret)
        return buffer.array()
    }
}