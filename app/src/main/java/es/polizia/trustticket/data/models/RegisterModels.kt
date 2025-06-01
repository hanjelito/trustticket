package es.polizia.trustticket.data.models

data class RegisterRequest(
    val name: String,
    val surname: String,
    val username: String,
    val phone: String,
    val email: String,
    val password: String
)

// ✅ Respuesta real del servidor
data class RegisterResponse(
    val auth_jwt: String
)

data class ErrorResponse(
    val detail: String? = null,
    val message: String? = null,
    val errors: Map<String, List<String>>? = null
)