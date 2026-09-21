package com.campuspass.app.api

import com.campuspass.app.data.model.AuthResponse
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

    @GET("users/me")
    suspend fun getMe(
        @Header("Authorization") token: String
    ): AuthResponse


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
}