package es.polizia.trustticket.data.network

import es.polizia.trustticket.data.models.LoginRequest
import es.polizia.trustticket.data.models.LoginResponse
import es.polizia.trustticket.data.models.RegisterRequest
import es.polizia.trustticket.data.models.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse
}
