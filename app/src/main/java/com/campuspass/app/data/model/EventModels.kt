package com.campuspass.app.data.model

data class Event(
    val eventId: Int,
    val title: String,
    val description: String? = null,
    val date: String,
    val time: String,
    val location: String,
    val capacity: Int,
    val category: String,
    val clubId: Int? = null,
    val organizerUserId: Int,
    val waitlistEnabled: Int = 1,
    val registeredCount: Int = 0,
    val status: String? = null
)

data class EventsResponse(
    val events: List<Event>
)

data class EventResponse(
    val event: Event
)

data class CreateEventRequest(
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val location: String,
    val capacity: Int,
    val category: String
)