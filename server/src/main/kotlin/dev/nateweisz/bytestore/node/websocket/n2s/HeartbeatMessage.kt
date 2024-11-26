package dev.nateweisz.bytestore.node.websocket.n2s

import dev.nateweisz.bytestore.node.data.NodeHeartBeat
import dev.nateweisz.bytestore.node.service.NodeService
import dev.nateweisz.bytestore.node.websocket.NodeSocketHandler
import org.springframework.web.socket.WebSocketSession
import java.nio.ByteBuffer

class HeartbeatMessage(val nodeService: NodeService) : N2SProtocolMessage {

    override fun handle(data: ByteBuffer, session: WebSocketSession) {
        val nodeId = NodeSocketHandler.sessionIdToNodeId[session.id] ?: throw RuntimeException("Somebody's getting *******")
        nodeService.heartbeat(nodeId, NodeHeartBeat(
            nodeId, // 4 bytes for the id
            data.getLong(), // 8 bytes
            data.getDouble(), // 8 bytes
            data.getLong() // 8 bytes
        ))
    }
}