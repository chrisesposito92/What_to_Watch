package com.example.whattowhat.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watched_items")
data class WatchedItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val movieId: Int?,
    val tvId: Int?,
    val title: String,
    val poster_path: String,
    val genre_ids: String,
    val release_date: String,
    val vote_average: Double,
    val isMovie: Boolean = true
)
