package dev.nateweisz.bytestore.node.data


data class RegistrationRequest(
    val ipAddress: String,
    val memory: Long, // Nodes must have a minimum of 3 GB of memory to be sufficient for building
    val registrationToken: String?
)