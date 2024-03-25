package com.example.notzeroranger.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL: String = "https://tpsuwflkyyzqgdkqexew.supabase.co/rest/v1/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: API by lazy {
        retrofit.create(API::class.java)
    }
}