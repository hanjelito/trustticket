package es.polizia.trustticket.data.models

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val auth_jwt: String
)
