package es.polizia.trustticket.data.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Interceptor opcional para agregar headers comunes (p. ej. Content-Type).
 * Si no lo necesitas, puedes omitir este interceptor.
 */
class HeadersInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Content-Type", "application/json")
            .build()
        return chain.proceed(request)
    }
}

object RetrofitClient {
    private const val BASE_URL = "https://trustticket.onrender.com/"

    // OkHttpClient (opcionalmente puedes agregar interceptores de logging, timeouts, etc.)
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HeadersInterceptor())
        .build()

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
