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
    val message: String?,
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