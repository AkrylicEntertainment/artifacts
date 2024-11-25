package dev.nateweisz.bytestore.agent

import kotlinx.serialization.Serializable


@Serializable
data class RegistrationRequest(
    val ipAddress: String,
    val memory: Long,
    val registrationToken: String?
)