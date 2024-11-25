package dev.nateweisz.bytestore.node.websocket.n2s

import org.springframework.web.socket.WebSocketSession
import java.nio.ByteBuffer

interface N2SProtocolMessage {
    fun handle(data: ByteBuffer, session: WebSocketSession)
}