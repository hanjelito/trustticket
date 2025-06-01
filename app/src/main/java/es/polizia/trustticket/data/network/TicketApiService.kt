// data/network/TicketApiService.kt
package es.polizia.trustticket.data.network

import es.polizia.trustticket.data.dto.MyTicketDTO
import es.polizia.trustticket.data.dto.QRResponse
import es.polizia.trustticket.data.models.BuyTicketRequest
import es.polizia.trustticket.data.models.BuyTicketResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TicketApiService {
    @POST("tickets/buy-ticket")
    suspend fun buyTicket(
        @Body request: BuyTicketRequest
    ): BuyTicketResponse

    @GET("tickets")
    suspend fun getMyTickets(): List<MyTicketDTO>

    @GET("tickets/{ticketId}/generate-temporal-qr")
    suspend fun generateTemporalQR(
        @Path("ticketId") ticketId: String
    ): QRResponse
}