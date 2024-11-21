package dev.nateweisz.bytestore.node.service

import dev.nateweisz.bytestore.node.ApprovalStage
import dev.nateweisz.bytestore.node.Node
import dev.nateweisz.bytestore.node.data.NodeHeartBeat
import dev.nateweisz.bytestore.node.State
import dev.nateweisz.bytestore.node.data.RegistrationRequest
import dev.nateweisz.bytestore.node.repository.NodeRegistrationRepository
import dev.nateweisz.bytestore.node.repository.NodeRepository
import jakarta.servlet.ServletRequest
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.Executors

@Service
class NodeService(val nodeRepository: NodeRepository, val registrationRepository: NodeRegistrationRepository) {
    // we will maintain a 100% cache of all nodes in the network
    val nodes: MutableList<Node> = mutableListOf()

    init {
        val scheduler = Executors.newScheduledThreadPool(1)
        scheduler.scheduleAtFixedRate({
            nodes.filter { node ->
                node.state == State.ACTIVE &&
                        (node.approvalStage == ApprovalStage.PRE_APPROVAL || node.approvalStage == ApprovalStage.MANUALLY_APPROVED)
            }.forEach { node ->
                if (!node.isHealthy()) {
                    node.state = State.INACTIVE
                    // TODO: add logging
                }
            }
        }, 0, 1, java.util.concurrent.TimeUnit.SECONDS)
    }

    fun heartbeat(nodeId: String, heartbeat: NodeHeartBeat) {
        val node = nodes.find { it.id == nodeId }
        if (node != null) {
            node.lastHeartbeat = System.currentTimeMillis()
            node.cpuUsage = heartbeat.cpuUsage
            node.memoryUsage = heartbeat.memoryUsage
        }
    }

    fun registerNode(registration: RegistrationRequest, request: ServletRequest): Node {
        return Node(
            id = generateId(),
            ip = request.remoteAddr,
            registrationToken = registration.registrationToken
        ).also {
            val token = registration.registrationToken?.let { registrationToken -> registrationRepository.findById(registrationToken) }
            if (token?.isPresent == true && !token.get().isExpired()) {
                it.approvalStage = ApprovalStage.PRE_APPROVAL
            }

            nodes.add(it)
            nodeRepository.save(it)
        }
    }

    private fun generateId(): String {
        return "node-" + UUID.randomUUID().toString().substring(0, 12)
    }
}