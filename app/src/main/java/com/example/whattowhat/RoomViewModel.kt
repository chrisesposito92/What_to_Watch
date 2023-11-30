package com.example.whattowhat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.whattowhat.model.WatchlistItem
import com.example.whattowhat.network.AppDatabase
import kotlinx.coroutines.launch

class RoomViewModel(application: Application) : AndroidViewModel(application) {

    private val db by lazy { AppDatabase.getDatabase(application) }

    // Expose the LiveData directly from the DAO
    val watchlist: LiveData<List<WatchlistItem>> = db.watchlistDao().getWatchlist()

    fun addToWatchlist(movieId: Int, title: String, posterPath: String, genre_ids: String, releaseDate: String, voteAverage: Double) {
        viewModelScope.launch {
            val watchlistItem = WatchlistItem(movieId = movieId, title = title, poster_path = posterPath, genre_ids = genre_ids, release_date = releaseDate, vote_average = voteAverage)
            db.watchlistDao().addToWatchlist(watchlistItem)
        }
    }

    fun removeFromWatchlist(movieId: Int) {
        viewModelScope.launch {
            db.watchlistDao().removeFromWatchlist(movieId)
        }
    }
}


