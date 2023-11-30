package com.example.whattowhat.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist_items")
data class WatchlistItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val movieId: Int,
    val title: String,
    val poster_path: String,
    val genre_ids: String,
    val release_date: String,
    val vote_average: Double
)
