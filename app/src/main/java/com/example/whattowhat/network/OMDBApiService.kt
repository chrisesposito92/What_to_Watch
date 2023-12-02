package com.example.whattowhat.network

import com.example.whattowhat.model.OMDbMovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OMDBApiService {
    @GET("/")
    suspend fun getMovieRatingFromOMDb(
        @Query("i") imdbId: String,
        @Query("apikey") apiKey: String = "645d98a7"
    ): Response<OMDbMovieResponse>

}