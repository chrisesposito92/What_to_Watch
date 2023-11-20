package com.example.whattowhat.model

object RatingData {
    val rating_initial = "All Ratings"
    val ratingList = (10 downTo 1).map { it.toString() }.toList()
    val years = listOf(rating_initial) + ratingList
}