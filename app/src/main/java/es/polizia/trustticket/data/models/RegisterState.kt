package es.polizia.trustticket.ui.viewModel

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data object Success : RegisterState()
    data class Error(
        val message: String,
        val fieldErrors: Map<String, List<String>> = emptyMap()
    ) : RegisterState()
}