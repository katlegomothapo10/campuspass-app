package com.campuspass.app.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    // 10.0.2.2 = localhost from emulator
    private const val BASE_URL = "http://10.0.2.2:3000/api/"

    val api: CampusPassApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CampusPassApi::class.java)
    }
}