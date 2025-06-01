// data/repository/TicketRepository.kt
package es.polizia.trustticket.data.repository

import es.polizia.trustticket.data.dto.MyTicketDTO
import es.polizia.trustticket.data.models.BuyTicketRequest
import es.polizia.trustticket.data.network.RetrofitClient
import es.polizia.trustticket.data.network.TicketApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class TicketRepository {

    // Instancia perezosa (lazy) de TicketApiService
    private val apiService: TicketApiService by lazy {
        RetrofitClient.instance.create(TicketApiService::class.java)
    }

    suspend fun buyTicket(eventId: String, seat: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            println("🎫 TicketRepository: Iniciando compra para evento $eventId, asiento $seat")

            val request = BuyTicketRequest(
                event_id = eventId,
                seat = seat
            )

            println("🎫 TicketRepository: Enviando request: $request")
            val response = apiService.buyTicket(request)
            println("🎫 TicketRepository: Respuesta recibida: $response")

            val isSuccess = response.success
            println("🎫 TicketRepository: Success = $isSuccess")

            isSuccess
        } catch (e: IOException) {
            // Error de red
            println("❌ TicketRepository: Error de red - ${e.message}")
            e.printStackTrace()
            false
        } catch (e: HttpException) {
            // Error HTTP (404, 500, etc.)
            println("❌ TicketRepository: Error HTTP ${e.code()} - ${e.message}")
            e.printStackTrace()
            false
        } catch (e: Exception) {
            // Cualquier otro error
            println("❌ TicketRepository: Error inesperado - ${e.message}")
            e.printStackTrace()
            false
        }
    }

    suspend fun getMyTickets(): List<MyTicketDTO> = withContext(Dispatchers.IO) {
        return@withContext try {
            println("🎫 TicketRepository: Obteniendo mis tickets")
            val tickets = apiService.getMyTickets()
            println("🎫 TicketRepository: ${tickets.size} tickets obtenidos")
            tickets
        } catch (e: IOException) {
            // Error de red
            println("❌ TicketRepository: Error de red al obtener tickets - ${e.message}")
            e.printStackTrace()
            emptyList()
        } catch (e: HttpException) {
            // Error HTTP (404, 500, etc.)
            println("❌ TicketRepository: Error HTTP ${e.code()} al obtener tickets - ${e.message}")
            e.printStackTrace()
            emptyList()
        } catch (e: Exception) {
            // Cualquier otro error
            println("❌ TicketRepository: Error inesperado al obtener tickets - ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
}