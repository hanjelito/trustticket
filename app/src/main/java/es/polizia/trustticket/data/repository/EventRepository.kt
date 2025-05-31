package es.polizia.trustticket.data.repository

import android.content.Context
import android.util.Log
import es.polizia.trustticket.data.dto.EventDTO
import es.polizia.trustticket.utils.loadEventFromJson

class EventRepository(private val context: Context) {

    fun getEvents(): List<EventDTO> {
        Log.d("EventRepository", "=== REPOSITORY: Starting getEvents() ===")

        return try {
            val events = context.loadEventFromJson()

            if (events.isEmpty()) {
                Log.w("EventRepository", "⚠️ No events loaded from JSON")
            } else {
                Log.d("EventRepository", "✅ Successfully loaded ${events.size} events")
            }

            events
        } catch (e: Exception) {
            Log.e("EventRepository", "❌ Exception in getEvents()", e)
            emptyList()
        }
    }
}