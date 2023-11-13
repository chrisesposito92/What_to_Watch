package com.example.whattowhat.model

data class MovieDiscoverResponse(
    val page: Int,
    val results: List<MovieItem>,
    val total_pages: Int,
    val total_results: Int
)