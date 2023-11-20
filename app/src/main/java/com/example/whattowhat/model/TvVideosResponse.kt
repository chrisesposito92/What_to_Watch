package com.example.whattowhat.model

import com.google.gson.annotations.SerializedName

data class TvVideosResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("results") val results: List<VideoResult>
)