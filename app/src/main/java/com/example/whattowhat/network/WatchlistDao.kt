package com.example.whattowhat.network

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.whattowhat.model.WatchlistItem

@Dao
interface WatchlistDao {
    @Insert
    suspend fun addToWatchlist(watchlistItem: WatchlistItem)

    @Query("SELECT * FROM watchlist_items")
    fun getWatchlist(): LiveData<List<WatchlistItem>>

    @Query("SELECT * FROM watchlist_items WHERE movieId = :movieId")
    fun getWatchlistItem(movieId: Int): WatchlistItem

    @Query("DELETE FROM watchlist_items WHERE movieId = :movieId")
    suspend fun removeFromWatchlist(movieId: Int)

}
