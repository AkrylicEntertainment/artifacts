package dev.nateweisz.bytestore.node.data

data class NodeHeartBeat(
    val nodeId: String,
    val timestamp: Long,
    val cpuUsage: Double,
    val memoryUsage: Long
)