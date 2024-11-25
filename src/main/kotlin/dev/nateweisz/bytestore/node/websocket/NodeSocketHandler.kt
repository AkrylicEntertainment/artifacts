package dev.nateweisz.bytestore.node.websocket

import dev.nateweisz.bytestore.node.ApprovalStage
import dev.nateweisz.bytestore.node.State
import dev.nateweisz.bytestore.node.repository.NodeRepository
import dev.nateweisz.bytestore.node.service.NodeService
import dev.nateweisz.bytestore.node.websocket.s2n.S2NProtocolMessage
import org.springframework.stereotype.Component
import org.springframework.web.socket.*
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.nio.ByteBuffer

@Component
class NodeSocketHandler(
    val nodeService: NodeService,
    val protocolReader: ProtocolReader,
    private val nodeRepository: NodeRepository
) : TextWebSocketHandler() {

    companion object {
        // I don't feel like auto wiring this into all the protocol message's, so I'm just going to make it static
        val sessions = mutableMapOf<String, WebSocketSession>()
        val sessionIdToNodeId = mutableMapOf<String, String>()
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        // TODO: validate the node id and check if it's already connected or if it has even been authenticated also validate it
        val nodeId = session.uri?.query?.substringAfter("nodeId=") ?: return session.close(CloseStatus(4001, "Missing nodeId"))
        val node = nodeService.nodes.find { it.id == nodeId } ?: return session.close(CloseStatus(4002, "Invalid nodeId"))
        if (sessions.containsKey(nodeId)) return session.close(CloseStatus(4003, "Node already connected"))
        if (node.approvalStage == ApprovalStage.UNAPPROVED) return session.close(CloseStatus(4004, "Node not approved"))

        node.state = State.ACTIVE
        nodeRepository.save(node)

        sessions[nodeId] = session
        sessionIdToNodeId[session.id] = nodeId
    }

    override fun handleBinaryMessage(session: WebSocketSession, message: BinaryMessage) {
        val packetId = message.payload.getInt()
        val protocol = protocolReader.n2sProtocol[packetId] ?: return // unknown packet
        protocol.handle(message.payload, session)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        // set node state to inactive
        // TODO ^^^
        sessions.values.remove(session)
    }

    // Send a message to a specific node
    fun sendMessageToNode(nodeId: String, id: Int, message: S2NProtocolMessage) {
        val session = sessions[nodeId]
        if (session?.isOpen == true) {
            val messageBytes = message.write()
            val payload = ByteBuffer.allocate(4 + messageBytes.size)
            payload.putInt(id)
            payload.put(messageBytes)
            session.sendMessage(BinaryMessage(payload.array()))
        }
    }
}