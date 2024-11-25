package dev.nateweisz.bytestore.node.websocket.n2s

import dev.nateweisz.bytestore.node.websocket.getString
import dev.nateweisz.bytestore.project.build.BuildService
import dev.nateweisz.bytestore.project.build.BuildStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession
import java.nio.ByteBuffer

class BuildFinishedMessage(val buildService: BuildService) : N2SProtocolMessage {

    override fun handle(data: ByteBuffer, session: WebSocketSession) {
        val status: BuildStatus = BuildStatus.valueOf(data.getString())

        when (status) {
            BuildStatus.SUCCESS -> {
                // Report success to build service
            }

            BuildStatus.FAILED -> {
                // Report failure to
            }
            else -> {} // Has someone hijacked our NODES!?? this should never be sent
        }
    }
}