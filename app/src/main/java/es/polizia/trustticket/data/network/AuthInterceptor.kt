// data/network/AuthInterceptor.kt
package es.polizia.trustticket.data.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = SessionManager.authToken

        // ⭐ LOG TEMPORAL para debuggear
        println("🔑 Token en interceptor: $token")

        return if (token.isNullOrBlank()) {
            println("⚠️ No hay token, enviando petición sin Authorization")
            chain.proceed(originalRequest)
        } else {
            val newRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            println("✅ Añadiendo header Authorization: Bearer $token")
            chain.proceed(newRequest)
        }
    }
}