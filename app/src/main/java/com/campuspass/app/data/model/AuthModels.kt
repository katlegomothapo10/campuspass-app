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
    val success: Boolean = false,
    val message: String? = null,
    val token: String? = null,
    val user: User? = null
)

data class User(
    val userId: Int,
    val name: String,
    val email: String,
    val studentNumber: String? = null,
    val profileImage: String? = null,
    val language: String? = null,
    val role: String,
    val notificationsEnabled: Int? = null,
    val biometricEnabled: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)