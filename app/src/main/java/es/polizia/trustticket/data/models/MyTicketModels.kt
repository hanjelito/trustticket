package es.polizia.trustticket.data.dto

import com.google.gson.annotations.SerializedName

data class MyTicketDTO(
    val id: String,
    val seat: String,
    val event: TicketEventDTO
)

data class TicketEventDTO(
    @SerializedName("imageUrl")
    val imageUrl: String,
    val name: String,
    val date: String,
    @SerializedName("location_name")
    val locationName: String
)