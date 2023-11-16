package com.example.whattowhat.model

import com.google.gson.annotations.SerializedName
data class VideoResult(
    @SerializedName("id") val id: String,
    @SerializedName("key") val key: String,
    @SerializedName("name") val name: String,
    @SerializedName("site") val site: String,
    @SerializedName("size") val size: Int,
    @SerializedName("type") val type: String
) {
    fun isYoutubeTrailer() = site == "YouTube" && type == "Trailer"
}