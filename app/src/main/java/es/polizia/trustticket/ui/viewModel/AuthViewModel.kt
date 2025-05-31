package es.polizia.trustticket.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.polizia.trustticket.data.models.LoginRequest
import es.polizia.trustticket.data.network.AuthService
import es.polizia.trustticket.data.network.RetrofitClient
import es.polizia.trustticket.data.network.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class AuthViewModel : ViewModel() {
    // Estado observable para la UI
    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState

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

                // Actualizamos el estado a Success con el token
                _loginState.value = AuthState.Success(auth_jwt)

                // Aquí puedes, opcionalmente, guardar el token en DataStore / SharedPreferences / EncryptedStorage
                // para usarlo en futuras peticiones:
                // saveToken(token)

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
     * Opcional: función para resetear el estado a Idle (por ejemplo, si cierras sesión).
     */
    fun resetState() {
        _loginState.value = AuthState.Idle
    }

    fun logout() {
        // Borra el JWT de memoria
        SessionManager.authToken = null
        // Reiniciamos el estado de login para que sea Idle
        _loginState.value = AuthState.Idle
    }
}
