package dev.nateweisz.bytestore.node.websocket.s2n

interface S2NProtocolMessage {
    fun write(): ByteArray
}