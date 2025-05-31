package es.polizia.trustticket.data.repository

import android.content.Context

import es.polizia.trustticket.data.dto.UserDto

class UserRepository(private val context: Context) {

    fun getUser(id: String): String? {
        // Implementación temporal
        return "User $id"
    }
}