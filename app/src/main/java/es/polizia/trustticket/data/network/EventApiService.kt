// data/network/EventApiService.kt
package es.polizia.trustticket.data.network

import es.polizia.trustticket.data.dto.EventDTO
import retrofit2.http.GET

interface EventApiService {
    @GET("events")
    suspend fun getEvents(): List<EventDTO>
}
