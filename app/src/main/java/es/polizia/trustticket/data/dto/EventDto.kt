package es.polizia.trustticket.data.dto

import android.R

data class EventDTO(
    val id: String,
    val name: String,
    val description: String,
    val startDatetime: String,   // ISO-8601, p.ej. "2025-06-15T18:00"
    val endDatetime: String,     // ISO-8601
    val category: String,
    val locationName: String,
    val locationAddress: String,
    val latitude: Double,
    val longitude: Double,
    val nearestMetro: String,
    val priceMin: Int,
    val priceMax: Int,
    val currency: String,
    val organizer: String,
    val contactPhone: String,
    val contactEmail: String,
    val contactWebsite: String,
    val tags: List<String>,
    val imageUrl: String
)

data class EventsResponseDTO(
    val events: List<EventDTO>
)
