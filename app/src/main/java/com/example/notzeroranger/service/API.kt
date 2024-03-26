package com.example.notzeroranger.service

import com.example.notzeroranger.highscore.HighScore
import com.example.notzeroranger.Utils
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface API {
    @GET("score")
    @Headers("apikey: ${Utils.API_KEY}")
    fun getData(
        @Query("limit") limit: Int,
        @Query("order") order: String
    ): Call<ArrayList<HighScore>>

    @POST("score")
    @Headers("apikey: ${Utils.API_KEY}")
    fun pushData(@Body body: HighScore): Call<HighScore>
}