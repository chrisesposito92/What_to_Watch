package com.example.whattowhat.network

import com.example.whattowhat.model.GptPrompt
import com.example.whattowhat.model.GptResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIService {
    @Headers("Authorization: Bearer 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")
    @POST("v1/engines/text-davinci-003/completions")
    fun getGptResponse(@Body prompt: GptPrompt): Call<GptResponse>
}