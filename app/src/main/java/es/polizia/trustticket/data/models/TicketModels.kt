package es.polizia.trustticket.data.models

data class BuyTicketRequest(
    val event_id: String,
    val seat: String
)

data class BuyTicketResponse(
    val success: Boolean,
    val ticket_id: String? = null,
    val message: String? = null
)