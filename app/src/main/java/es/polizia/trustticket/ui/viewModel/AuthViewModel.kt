// ui/viewModel/AuthViewModel.kt
package es.polizia.trustticket.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import es.polizia.trustticket.data.models.ErrorResponse
import es.polizia.trustticket.data.models.LoginRequest
import es.polizia.trustticket.data.models.RegisterRequest
import es.polizia.trustticket.data.network.AuthService
import es.polizia.trustticket.data.network.RetrofitClient
import es.polizia.trustticket.data.network.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class AuthViewModel : ViewModel() {
    // Estados observables para la UI
    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    // Instanciamos el servicio de Retrofit
    private val authService: AuthService = RetrofitClient.instance.create(AuthService::class.java)

    /**
     * Lanza una coroutine para hacer la petición de login.
     */
    fun login(username: String, password: String) {
        // Evitar invocar varias veces en paralelo
        if (_loginState.value is AuthState.Loading) return

        viewModelScope.launch {
            _loginState.value = AuthState.Loading

            try {
                // Construimos el request
                val request = LoginRequest(username = username, password = password)
                // Llamamos al endpoint
                val response = authService.login(request)

                // Si llegamos aquí, el login fue exitoso
                val auth_jwt = response.auth_jwt

                // ⭐ IMPORTANTE: Guardamos el token en SessionManager
                SessionManager.authToken = auth_jwt

                // Actualizamos el estado a Success con el token
                _loginState.value = AuthState.Success(auth_jwt)

            } catch (e: IOException) {
                // Error de conexión (timeout, no internet, etc.)
                _loginState.value = AuthState.Error("Error de red: ${e.localizedMessage}")
            } catch (e: HttpException) {
                // El servidor devolvió un código HTTP que no es 2xx
                val code = e.code()
                if (code == 401 || code == 400) {
                    _loginState.value = AuthState.Error("Usuario o contraseña inválidos")
                } else {
                    _loginState.value = AuthState.Error("Error del servidor: código $code")
                }
            } catch (e: Exception) {
                // Cualquier otro error
                _loginState.value = AuthState.Error("Error inesperado: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Lanza una coroutine para hacer la petición de registro.
     */
    fun register(
        name: String,
        surname: String,
        username: String,
        phone: String,
        email: String,
        password: String
    ) {
        // Evitar invocar varias veces en paralelo
        if (_registerState.value is RegisterState.Loading) return

        viewModelScope.launch {
            _registerState.value = RegisterState.Loading

            try {
                val request = RegisterRequest(
                    name = name.trim(),
                    surname = surname.trim(),
                    username = username.trim(),
                    phone = phone.trim(),
                    email = email.trim(),
                    password = password
                )

                val response = authService.register(request)

                // ✅ CORREGIDO: Si llegamos aquí sin excepción y tenemos JWT, es éxito
                if (response.auth_jwt.isNotBlank()) {
                    println("✅ Registro exitoso con JWT: ${response.auth_jwt}")
                    _registerState.value = RegisterState.Success
                } else {
                    _registerState.value = RegisterState.Error("No se recibió token de autenticación")
                }

            } catch (e: IOException) {
                _registerState.value = RegisterState.Error("Error de red: ${e.localizedMessage}")
            } catch (e: HttpException) {
                // Intentar parsear errores del servidor
                try {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)

                    val message = when (e.code()) {
                        400 -> "Datos inválidos"
                        409 -> "El usuario o email ya existe"
                        422 -> "Error de validación"
                        else -> "Error del servidor: ${e.code()}"
                    }

                    _registerState.value = RegisterState.Error(
                        message = errorResponse.detail ?: errorResponse.message ?: message,
                        fieldErrors = errorResponse.errors ?: emptyMap()
                    )
                } catch (parseException: Exception) {
                    _registerState.value = RegisterState.Error("Error del servidor: ${e.code()}")
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Error inesperado: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Resetear los estados
     */
    fun resetLoginState() {
        _loginState.value = AuthState.Idle
    }

    fun resetRegisterState() {
        _registerState.value = RegisterState.Idle
    }

    fun logout() {
        // Borra el JWT de memoria
        SessionManager.authToken = null
        // Reiniciamos el estado de login para que sea Idle
        _loginState.value = AuthState.Idle
        _registerState.value = RegisterState.Idle
    }
}