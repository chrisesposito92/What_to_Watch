package com.example.whattowhat.model

data class TvDiscoverResponse(
    val page: Int,
    val results: List<TvItem>,
    val total_pages: Int,
    val total_results: Int
)