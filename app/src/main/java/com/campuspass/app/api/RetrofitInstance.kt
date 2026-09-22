package com.campuspass.app.api

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // 10.0.2.2 = the host computer's localhost when using the Android Emulator
    private const val BASE_URL = "http://127.0.0.1:3000/api/"

    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d("CampusPassAPI", message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val api: CampusPassApi by lazy {
        Log.d("CampusPassAPI", "Creating Retrofit with BASE_URL=$BASE_URL")

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CampusPassApi::class.java)
    }
}