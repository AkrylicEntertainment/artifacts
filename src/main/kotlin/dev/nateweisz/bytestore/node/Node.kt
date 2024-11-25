package dev.nateweisz.bytestore.node

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import kotlin.time.Duration

private val maxHeartBeatTheshold: Long = 10 * 1000; // 10 seconds

/**
 * A node represent a connected docker container that is running a bytestore-builder instance.
 * These nodes are responsible for building open source GitHub repositories. All nodes must be
 * approved by the bytestore team before they can be added to the network.
 */
@Document(collection = "nodes")
data class Node(
    @Id
    val id: String,
    val ip: String,
    var approvalStage: ApprovalStage = ApprovalStage.UNAPPROVED,
    val registrationToken: String?,

    // node health and performance metrics
    var state: State = State.ACTIVE,
    var lastHeartbeat: Long? = -1,
    var cpuUsage: Double? = 0.0,
    var memoryUsage: Long? = 0L
) {
    fun isHealthy(): Boolean {
        return lastHeartbeat?.let { System.currentTimeMillis() - it < maxHeartBeatTheshold } ?: false
    }
}

enum class State {
    ACTIVE, INACTIVE
}