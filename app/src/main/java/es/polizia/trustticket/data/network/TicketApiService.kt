package es.polizia.trustticket.data.network

import es.polizia.trustticket.data.dto.MyTicketDTO
import es.polizia.trustticket.data.models.BuyTicketRequest
import es.polizia.trustticket.data.models.BuyTicketResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TicketApiService {
    @POST("tickets/buy-ticket")
    suspend fun buyTicket(
        @Body request: BuyTicketRequest
    ): BuyTicketResponse

    @GET("tickets")
    suspend fun getMyTickets(): List<MyTicketDTO>
}