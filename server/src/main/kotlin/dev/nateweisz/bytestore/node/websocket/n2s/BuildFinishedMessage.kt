package dev.nateweisz.bytestore.node.websocket.n2s

import dev.nateweisz.bytestore.node.websocket.getString
import dev.nateweisz.bytestore.project.build.BuildService
import dev.nateweisz.bytestore.project.build.BuildStatus
import org.springframework.web.socket.WebSocketSession
import java.nio.ByteBuffer

class BuildFinishedMessage(private val buildService: BuildService) : N2SProtocolMessage {

    override fun handle(data: ByteBuffer, session: WebSocketSession) {
        val status: BuildStatus = BuildStatus.valueOf(data.getString())
        val logs = data.getString()

        when (status) {
            BuildStatus.SUCCESS -> {
                // Report success to build service
                buildService.finishBuild(session.id, BuildStatus.SUCCESS, logs, data.getString())
            }

            BuildStatus.FAILED -> {
                // Report failure to
                buildService.finishBuild(session.id, BuildStatus.FAILED, logs)
            }
            else -> {} // Has someone hijacked our NODES!?? this should never be sent
        }
    }
}