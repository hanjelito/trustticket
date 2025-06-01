// ui/viewModel/MyTicketsViewModel.kt
package es.polizia.trustticket.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.polizia.trustticket.data.dto.MyTicketDTO
import es.polizia.trustticket.data.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MyTicketsViewModel(
    private val repository: TicketRepository
) : ViewModel() {

    private val _tickets = MutableStateFlow<List<MyTicketDTO>>(emptyList())
    val tickets: StateFlow<List<MyTicketDTO>> = _tickets

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadMyTickets()
    }

    fun loadMyTickets() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val myTickets = repository.getMyTickets()
                _tickets.value = myTickets
                println("🎫 MyTicketsViewModel: ${myTickets.size} tickets cargados")
            } catch (e: Exception) {
                _error.value = "Error al cargar los tickets: ${e.localizedMessage}"
                println("❌ MyTicketsViewModel: Error - ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        loadMyTickets()
    }
}