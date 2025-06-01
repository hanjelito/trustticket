package es.polizia.trustticket.data.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
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
    private const val BASE_URL = "https://trustticket.nest0r.dev/"

    // 1) Interceptor que añade Content-Type
    private val headersInterceptor = HeadersInterceptor()

    // 2) Interceptor que añade “Authorization: Bearer <jwt>”
    private val authInterceptor = AuthInterceptor()

    // 3) (Opcional) interceptor de logging para ver peticiones en Logcat
    //    import okhttp3.logging.HttpLoggingInterceptor
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(headersInterceptor)
        .addInterceptor(authInterceptor)   // <— ahora incluye el header Authorization
        .addInterceptor(loggingInterceptor) // <— opcional, pero muy útil para depurar
        .build()

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
