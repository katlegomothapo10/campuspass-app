package com.campuspass.app.api

import com.campuspass.app.data.model.AttendanceScanRequest
import com.campuspass.app.data.model.AttendanceScanResponse
import com.campuspass.app.data.model.AuthResponse
import com.campuspass.app.data.model.CreateEventRequest
import com.campuspass.app.data.model.EventResponse
import com.campuspass.app.data.model.EventsResponse
import com.campuspass.app.data.model.LoginRequest
import com.campuspass.app.data.model.RegisterRequest
import com.campuspass.app.data.model.SsoRequest
import com.campuspass.app.data.model.TicketQrResponse
import com.campuspass.app.data.model.TicketResponse
import com.campuspass.app.data.model.TicketsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface CampusPassApi {

    // =========================
    // AUTHENTICATION
    // =========================

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): AuthResponse

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): AuthResponse

    @POST("auth/sso")
    suspend fun ssoLogin(
        @Body request: SsoRequest
    ): AuthResponse

    /*
     * NOTE:
     * The current CampusPass backend does not expose /api/users/me.
     * This method is retained for compatibility with existing team code.
     * Do not rely on it for the organizer My Events flow.
     */
    @GET("users/me")
    suspend fun getMe(
        @Header("Authorization") token: String
    ): AuthResponse


    // =========================
    // EVENTS
    // =========================

    @GET("events")
    suspend fun getEvents(): EventsResponse

    @GET("events/{id}")
    suspend fun getEvent(
        @Path("id") eventId: Int
    ): EventResponse

    @GET("events/{id}")
    suspend fun getEventById(
        @Path("id") eventId: Int
    ): EventResponse

    @POST("events")
    suspend fun createEvent(
        @Header("Authorization") token: String,
        @Body request: CreateEventRequest
    ): EventResponse

    @POST("events/{id}/tickets")
    suspend fun registerForEvent(
        @Header("Authorization") token: String,
        @Path("id") eventId: Int
    ): TicketResponse


    // =========================
    // TICKETS
    // =========================

    @GET("tickets/me")
    suspend fun getMyTickets(
        @Header("Authorization") token: String
    ): TicketsResponse

    @GET("tickets/{id}")
    suspend fun getTicket(
        @Header("Authorization") token: String,
        @Path("id") ticketId: Int
    ): TicketResponse

    @GET("tickets/{id}/qr")
    suspend fun getTicketQr(
        @Header("Authorization") token: String,
        @Path("id") ticketId: Int
    ): TicketQrResponse


    // =========================
    // ATTENDANCE / QR SCANNING
    // =========================

    @POST("attendance/scan")
    suspend fun scanTicket(
        @Header("Authorization") token: String,
        @Body request: AttendanceScanRequest
    ): AttendanceScanResponse
}