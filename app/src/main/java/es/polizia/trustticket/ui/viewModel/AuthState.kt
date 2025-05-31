package es.polizia.trustticket.ui.viewModel

/**
 * Representa el estado de la operación de login en el ViewModel.
 */
sealed class AuthState {
    object Idle : AuthState()                         // todavía no se ha hecho nada
    object Loading : AuthState()                      // en proceso de login
    data class Success(val token: String) : AuthState() // login exitoso, guardamos el token
    data class Error(val message: String) : AuthState() // ocurrió un error (credenciales inválidas, red, server)
}