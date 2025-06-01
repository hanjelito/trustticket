package es.polizia.trustticket.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.polizia.trustticket.data.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TicketDetailViewModel(
    private val repository: TicketRepository
) : ViewModel() {

    private val _qrResult = MutableStateFlow<QRResult>(QRResult.Loading)
    val qrResult: StateFlow<QRResult> = _qrResult

    fun generateQR(ticketId: String) {
        viewModelScope.launch {
            _qrResult.value = QRResult.Loading

            try {
                println("🎫 TicketDetailViewModel: Generando QR para ticket $ticketId")
                val result = repository.generateTemporalQR(ticketId)
                _qrResult.value = result
                println("🎫 TicketDetailViewModel: Resultado QR: $result")
            } catch (e: Exception) {
                println("❌ TicketDetailViewModel: Error inesperado - ${e.message}")
                _qrResult.value = QRResult.Error("Error inesperado: ${e.localizedMessage}")
            }
        }
    }
}