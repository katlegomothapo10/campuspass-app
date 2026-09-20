package com.campuspass.app

import com.campuspass.app.data.model.LoginRequest
import com.campuspass.app.data.model.RegisterRequest
import com.campuspass.app.data.model.SsoRequest
import org.junit.Assert.assertEquals
import org.junit.Test

class UserPreferencesTest {

    @Test
    fun testTokenKey_hasCorrectValue() {
        assertEquals("jwt_token", UserPreferences.TOKEN_KEY.name)
    }

    @Test
    fun testUserNameKey_hasCorrectValue() {
        assertEquals("user_name", UserPreferences.USER_NAME_KEY.name)
    }

    @Test
    fun testUserEmailKey_hasCorrectValue() {
        assertEquals("user_email", UserPreferences.USER_EMAIL_KEY.name)
    }

    @Test
    fun testLoginRequest_createsCorrectly() {
        val request = LoginRequest(
            email = "test@test.com",
            password = "password123"
        )
        assertEquals("test@test.com", request.email)
        assertEquals("password123", request.password)
    }

    @Test
    fun testRegisterRequest_createsCorrectly() {
        val request = RegisterRequest(
            name = "Test User",
            email = "test@test.com",
            studentNumber = "ST12345",
            password = "password123"
        )
        assertEquals("Test User", request.name)
        assertEquals("ST12345", request.studentNumber)
    }

    @Test
    fun testSsoRequest_createsCorrectly() {
        val request = SsoRequest(
            idToken = "test_token",
            mode = "register"
        )
        assertEquals("test_token", request.idToken)
        assertEquals("register", request.mode)
    }
}