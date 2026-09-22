package com.campuspass.app.api

import com.campuspass.app.data.model.AuthResponse
import com.campuspass.app.data.model.CreateEventRequest
import com.campuspass.app.data.model.EventResponse
import com.campuspass.app.data.model.EventsResponse
import com.campuspass.app.data.model.LoginRequest
import com.campuspass.app.data.model.RegisterRequest
import com.campuspass.app.data.model.SsoRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface CampusPassApi {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/sso")
    suspend fun ssoLogin(@Body request: SsoRequest): AuthResponse

    @GET("users/me")
    suspend fun getMe(@Header("Authorization") token: String): AuthResponse

    @GET("events")
    suspend fun getEvents(): EventsResponse

    @GET("events/{id}")
    suspend fun getEventById(@Path("id") id: Int): EventResponse

    @POST("events")
    suspend fun createEvent(
        @Header("Authorization") token: String,
        @Body request: CreateEventRequest
    ): EventResponse
}