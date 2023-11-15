package com.example.whattowhat.model

object SortOptions {
    val sortOptions = listOf(
        SortOption("popularity.desc", "Popular (High->Low)"),
        SortOption("popularity.asc", "Popular (Low->High)"),
        SortOption("vote_average.desc", "Ratings (High->Low)"),
        SortOption("vote_average.asc", "Ratings (Low->High)"),
        SortOption("primary_release_date.desc", "Release Date (New->Old)"),
        SortOption("primary_release_date.asc", "Release Date (Old->New)"),
        SortOption("vote_count.desc", "Rating Count (High->Low)"),
        SortOption("vote_count.asc", "Rating Count (Low->High)"),
        SortOption("revenue.desc", "Revenue (High->Low)"),
        SortOption("revenue.asc", "Revenue (Low->High)"),
    )
}