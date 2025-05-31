// EventDto.kt
package es.polizia.trustticket.data.dto

import com.google.gson.annotations.SerializedName

data class EventDTO(
    val id: String,
    val name: String,
    val description: String,

    @SerializedName("start_datetime")
    val startDatetime: String,

    @SerializedName("end_datetime")
    val endDatetime: String,

    val category: String,

    @SerializedName("location_name")
    val locationName: String,

    @SerializedName("location_address")
    val locationAddress: String,

    val latitude: Double,
    val longitude: Double,

    @SerializedName("nearest_metro")
    val nearestMetro: String,

    @SerializedName("price_min")
    val priceMin: Int,

    @SerializedName("price_max")
    val priceMax: Int,

    val currency: String,
    val organizer: String,

    @SerializedName("contact_phone")
    val contactPhone: String,

    @SerializedName("contact_email")
    val contactEmail: String,

    @SerializedName("contact_website")
    val contactWebsite: String,

    val tags: List<String>,

    @SerializedName("image_url")
    val imageUrl: String
)
