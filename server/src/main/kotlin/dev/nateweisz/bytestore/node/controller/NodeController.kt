package dev.nateweisz.bytestore.node.controller

import dev.nateweisz.bytestore.node.ApprovalStage
import dev.nateweisz.bytestore.node.Node
import dev.nateweisz.bytestore.node.data.RegistrationRequest
import dev.nateweisz.bytestore.node.service.NodeService
import jakarta.servlet.ServletRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/nodes")
class NodeController(private val nodeService: NodeService) {

    @GetMapping("")
    fun allNodes(): List<Node> {
        return nodeService.nodes
    }

    @PostMapping("/register")
    fun registerNode(@RequestBody registration: RegistrationRequest, request: ServletRequest): Node {
        return nodeService.registerNode(registration, request)
    }

    @PostMapping("/{nodeId}/approve")
    fun approveNode(@PathVariable nodeId: String) {
        val node = nodeService.nodes.find { it.id == nodeId }
        if (node != null) {
            node.approvalStage = ApprovalStage.MANUALLY_APPROVED
        }
    }
}