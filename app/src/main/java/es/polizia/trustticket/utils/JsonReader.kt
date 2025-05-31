package es.polizia.trustticket.utils

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import es.polizia.trustticket.R
import es.polizia.trustticket.data.dto.EventDTO
import es.polizia.trustticket.data.dto.EventsResponseDTO

fun Context.loadEventFromJson(): List<EventDTO> {
    return try {
        Log.d("JsonUtils", "Loading events from raw resources...")

        val inputStream = resources.openRawResource(R.raw.events)
        val json = inputStream.bufferedReader().use { it.readText() }
        Log.d("JsonUtils", "JSON loaded successfully, length: ${json.length}")

        val gson = Gson()
        val eventsResponse = gson.fromJson(json, EventsResponseDTO::class.java)
        Log.d("JsonUtils", "Parsed successfully with ${eventsResponse.events.size} events")

        eventsResponse.events
    } catch (e: Exception) {
        Log.e("JsonUtils", "Error loading events from JSON", e)
        emptyList()
    }
}