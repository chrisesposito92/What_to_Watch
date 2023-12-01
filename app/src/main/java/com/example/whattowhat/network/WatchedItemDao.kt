package com.example.whattowhat.network

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.whattowhat.model.WatchedItem

@Dao
interface WatchedItemDao {
    @Insert
    suspend fun addToWatched(watchlistItem: WatchedItem)

    @Query("SELECT * FROM watched_items WHERE isMovie = 1")
    fun getWatchedMovies(): LiveData<List<WatchedItem>>

    @Query("SELECT * FROM watched_items WHERE isMovie = 0")
    fun getWatchedTvShows(): LiveData<List<WatchedItem>>

    @Query("SELECT * FROM watched_items WHERE movieId = :movieId")
    fun getWatchedMovie(movieId: Int): WatchedItem

    @Query("SELECT * FROM watched_items WHERE movieId = :tvId")
    fun getWatchedTvvShow(tvId: Int): WatchedItem

    @Query("DELETE FROM watched_items WHERE movieId = :movieId")
    suspend fun removeWatchedMovie(movieId: Int)

    @Query("DELETE FROM watched_items WHERE tvId = :tvId")
    suspend fun removeWatchedTvShow(tvId: Int)

}
