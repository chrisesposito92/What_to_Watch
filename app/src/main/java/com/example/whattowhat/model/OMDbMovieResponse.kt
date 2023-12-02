package com.example.whattowhat.model

import com.google.gson.annotations.SerializedName

data class OMDbMovieResponse(
    @SerializedName("Ratings") val ratings: List<Rating>?,
    @SerializedName("Metascore") val metascore: String?,
    @SerializedName("imdbRating") val imdbRating: String,
    @SerializedName("imdbVotes") val imdbVotes: String?,
    @SerializedName("imdbID") val imdbID: String?,
)

