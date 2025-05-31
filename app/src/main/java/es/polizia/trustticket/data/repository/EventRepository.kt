// data/repository/EventRepository.kt
package es.polizia.trustticket.data.repository

import es.polizia.trustticket.data.dto.EventDTO
import es.polizia.trustticket.data.network.EventApiService
import es.polizia.trustticket.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class EventRepository {

    // Instancia perezosa (lazy) de EventApiService
    private val apiService: EventApiService by lazy {
        RetrofitClient.instance.create(EventApiService::class.java)
    }

    // Ahora no recibe token: el interceptor lo añade automáticamente
    suspend fun getEvents(): List<EventDTO> = withContext(Dispatchers.IO) {
        return@withContext try {
            apiService.getEvents()
        } catch (e: IOException) {
            // Error de red
            e.printStackTrace()
            emptyList()
        } catch (e: HttpException) {
            // Error HTTP (404, 500, etc.)
            e.printStackTrace()
            emptyList()
        } catch (e: Exception) {
            // Cualquier otro error
            e.printStackTrace()
            emptyList()
        }
    }
}
