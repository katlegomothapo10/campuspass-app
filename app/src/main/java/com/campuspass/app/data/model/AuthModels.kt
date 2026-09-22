package com.campuspass.app.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val studentNumber: String,
    val password: String
)

data class SsoRequest(
    val idToken: String,
    val mode: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val user: User?
)

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val student_number: String?,
    val role: String
)

data class Event(
    val id: Int,
    val title: String,
    val description: String?,
    val date: String,
    val time: String,
    val location: String,
    val capacity: Int,
    val category: String?,
    val clubId: Int?,
    val status: String?
)

data class EventsResponse(
    val success: Boolean,
    val events: List<Event>?
)

data class EventResponse(
    val success: Boolean,
    val event: Event?
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