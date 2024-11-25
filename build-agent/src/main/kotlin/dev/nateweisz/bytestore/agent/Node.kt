package dev.nateweisz.bytestore.agent

import kotlinx.serialization.Serializable

@Serializable
data class Node(
    val id: String,
)