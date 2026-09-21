package com.campuspass.app.data.model

data class Ticket(
    val ticketId: Int,
    val eventId: Int,
    val userId: Int,
    val qrCode: String,
    val status: String,
    val registrationDate: String,
    val eventTitle: String? = null,
    val eventDate: String? = null,
    val eventTime: String? = null,
    val eventLocation: String? = null
)

data class TicketsResponse(
    val tickets: List<Ticket>
)

data class TicketResponse(
    val ticket: Ticket
)

data class TicketQrResponse(
    val qrCode: String,
    val qrDataUrl: String
)