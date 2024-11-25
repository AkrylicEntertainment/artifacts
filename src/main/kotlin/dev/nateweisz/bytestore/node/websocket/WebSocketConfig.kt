package dev.nateweisz.bytestore.node.websocket

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.*

@Configuration
@EnableWebSocket
class WebSocketConfig : WebSocketConfigurer {

    @Autowired
    private lateinit var nodeSocketHandler: NodeSocketHandler

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(nodeSocketHandler, "/api/nodes/ws")
    }
}