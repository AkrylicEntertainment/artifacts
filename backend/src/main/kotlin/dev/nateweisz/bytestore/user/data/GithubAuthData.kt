package dev.nateweisz.bytestore.user.data

data class GithubCallbackRequest(
    val code: String,
    val state: String,
)

data class GithubCallbackResponse(

)
