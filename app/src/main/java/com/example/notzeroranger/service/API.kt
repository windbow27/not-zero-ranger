package com.example.notzeroranger.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface API {
    @GET("score")
    @Headers("apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRwc3V3ZmxreXl6cWdka3FleGV3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTA2MDM5NTYsImV4cCI6MjAyNjE3OTk1Nn0.kPCOaLTEPDuzkfXq5jSHa9PJIi1M2HQe_SGL3_EFAVQ")
    fun getData(
        @Query("limit") limit: Int,
    ): Call<ArrayList<PlayerScore>>
}