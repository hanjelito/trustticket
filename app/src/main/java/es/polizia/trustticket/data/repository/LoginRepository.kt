// data/repository/LoginRepository.kt
package es.polizia.trustticket.data.repository

import es.polizia.trustticket.data.models.LoginRequest
import es.polizia.trustticket.data.network.AuthService
import es.polizia.trustticket.data.network.RetrofitClient
import es.polizia.trustticket.data.network.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class LoginRepository {
    private val api: AuthService =
        RetrofitClient.instance.create(AuthService::class.java)

    suspend fun login(email: String, password: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = api.login(LoginRequest(email, password))
            // response.token es el JWT que viene del backend
            SessionManager.authToken = response.auth_jwt
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } catch (e: HttpException) {
            e.printStackTrace()
            false
        }
    }
}
