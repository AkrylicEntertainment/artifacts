package dev.nateweisz.bytestore.node.websocket

import dev.nateweisz.bytestore.node.service.NodeService
import dev.nateweisz.bytestore.node.websocket.n2s.BuildFinishedMessage
import dev.nateweisz.bytestore.node.websocket.n2s.HeartbeatMessage
import dev.nateweisz.bytestore.node.websocket.n2s.N2SProtocolMessage
import dev.nateweisz.bytestore.node.websocket.s2n.RequestBuildMessage
import dev.nateweisz.bytestore.node.websocket.s2n.S2NProtocolMessage
import dev.nateweisz.bytestore.project.build.BuildService
import okhttp3.Protocol
import org.json.JSONObject
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.nio.ByteBuffer

@Component
class ProtocolReader(nodeService: NodeService, buildService: BuildService) {

    // we should prob consider using a factory pattern instead of this since it's ugly af
    val n2sProtocol: Map<Int, N2SProtocolMessage> = mapOf(
        0x00 to HeartbeatMessage(nodeService),
        0x01 to BuildFinishedMessage(buildService),
    )
}

fun ByteBuffer.getString(): String {
    val length = int
    val bytes = ByteArray(length)
    get(bytes)
    return String(bytes)
}

fun ByteBuffer.writeString(string: String) {
    putInt(string.length)
    put(string.toByteArray())
}