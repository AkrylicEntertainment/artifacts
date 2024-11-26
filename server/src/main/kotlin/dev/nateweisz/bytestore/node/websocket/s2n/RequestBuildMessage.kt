package dev.nateweisz.bytestore.node.websocket.s2n

import dev.nateweisz.bytestore.node.websocket.writeString
import java.nio.ByteBuffer

class RequestBuildMessage(val owner: String, val repository: String, val commitHash: String) : S2NProtocolMessage {
    override fun write(): ByteArray {
        val buffer = ByteBuffer.allocate(4 + owner.length + 4 + repository.length + 4 + commitHash.length)
        buffer.writeString(owner)
        buffer.writeString(repository)
        buffer.writeString(commitHash)
        return buffer.array()
    }
}