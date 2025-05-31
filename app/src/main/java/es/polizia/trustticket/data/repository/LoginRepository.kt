package es.polizia.trustticket.data.repository

import android.content.Context

class LoginRepository(private val context: Context) {

    fun login(email: String, password: String): Boolean {
        // Implementación temporal
        return email.isNotEmpty() && password.isNotEmpty()
    }

    fun logout() {
        // Implementación temporal
    }
}
