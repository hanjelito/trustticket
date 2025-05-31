package es.polizia.trustticket.ui.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import es.polizia.trustticket.data.dto.EventDTO
import es.polizia.trustticket.data.repository.EventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EventRepository(application.applicationContext)

    private val _events = MutableStateFlow<List<EventDTO>>(emptyList())
    val events: StateFlow<List<EventDTO>> = _events.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        Log.d("EventsViewModel", "Initializing EventsViewModel")
        loadEvents()
    }

    private fun loadEvents() {
        Log.d("EventsViewModel", "Starting to load events")
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val eventsList = repository.getEvents()
                Log.d("EventsViewModel", "Loaded ${eventsList.size} events")
                eventsList.forEachIndexed { index, event ->
                    Log.d("EventsViewModel", "Event $index: ${event.name}")
                }
                _events.value = eventsList
            } catch (e: Exception) {
                Log.e("EventsViewModel", "Error loading events", e)
                _events.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshEvents() {
        Log.d("EventsViewModel", "Refreshing events")
        loadEvents()
    }
}