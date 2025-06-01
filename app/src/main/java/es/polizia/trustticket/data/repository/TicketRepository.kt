// data/repository/TicketRepository.kt
package es.polizia.trustticket.data.repository

import com.google.gson.Gson
import es.polizia.trustticket.data.dto.LocationErrorResponse
import es.polizia.trustticket.data.dto.MyTicketDTO
import es.polizia.trustticket.data.models.BuyTicketRequest
import es.polizia.trustticket.data.network.RetrofitClient
import es.polizia.trustticket.data.network.TicketApiService
import es.polizia.trustticket.ui.viewModel.QRResult
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

    suspend fun generateTemporalQR(ticketId: String): QRResult = withContext(Dispatchers.IO) {
        return@withContext try {
            println("🎫 TicketRepository: Generando QR temporal para ticket $ticketId")
            val response = apiService.generateTemporalQR(ticketId)
            println("🎫 TicketRepository: QR generado exitosamente")
            QRResult.Success(response.qrJwt)
        } catch (e: HttpException) {
            println("❌ TicketRepository: Error HTTP ${e.code()} al generar QR")
            when (e.code()) {
                403 -> {
                    // Error de ubicación
                    try {
                        val errorBody = e.response()?.errorBody()?.string()
                        val errorResponse = Gson().fromJson(errorBody, LocationErrorResponse::class.java)
                        println("❌ Error de ubicación: ${errorResponse.detail}")
                        QRResult.LocationError("No te encuentras en la ubicación del evento")
                    } catch (parseException: Exception) {
                        QRResult.LocationError("No te encuentras en la ubicación del evento")
                    }
                }
                else -> {
                    QRResult.Error("Error del servidor: ${e.code()}")
                }
            }
        } catch (e: IOException) {
            println("❌ TicketRepository: Error de red al generar QR - ${e.message}")
            QRResult.Error("Error de conexión. Verifica tu internet.")
        } catch (e: Exception) {
            println("❌ TicketRepository: Error inesperado al generar QR - ${e.message}")
            QRResult.Error("Error inesperado: ${e.localizedMessage}")
        }
    }
}