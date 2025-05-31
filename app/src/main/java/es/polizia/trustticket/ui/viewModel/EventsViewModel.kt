// ui/viewModel/EventsViewModel.kt
package es.polizia.trustticket.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.polizia.trustticket.data.dto.EventDTO
import es.polizia.trustticket.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventsViewModel(
    private val repository: EventRepository
) : ViewModel() {

    private val _events = MutableStateFlow<List<EventDTO>>(emptyList())
    val events: StateFlow<List<EventDTO>> = _events

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        refreshEvents()
    }

    fun refreshEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            // Llamamos a getEvents() sin pasar token explícito
            val lista = repository.getEvents()
            _events.value = lista
            _isLoading.value = false
        }
    }
}
