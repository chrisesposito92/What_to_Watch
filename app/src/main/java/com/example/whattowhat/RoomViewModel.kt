package com.example.whattowhat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.whattowhat.model.WatchlistItem
import com.example.whattowhat.network.AppDatabase
import kotlinx.coroutines.launch
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.whattowhat.model.MovieDetail
import com.example.whattowhat.model.WatchedItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    private val _isWatchlistItem = MutableLiveData<Boolean>()
    val isWatchlistItem: LiveData<Boolean> = _isWatchlistItem

    fun isInWatchlist(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val watchlistItem = db.watchlistDao().getWatchlistItem(movieId)
                Log.e("RoomViewModel", "Watchlist Item: $watchlistItem")
                if(watchlistItem != null) {
                    _isWatchlistItem.postValue(true)
                } else {
                    _isWatchlistItem.postValue(false)
                }
            } catch (e: Exception) {
                Log.e("RoomViewModel", "Error: $e")
                _isWatchlistItem.postValue(false)
            }
        }
    }

    // Expose the LiveData directly from the DAO
    val watchedMovielist: LiveData<List<WatchedItem>> = db.watchedItemDao().getWatchedMovies()

    fun addToWatchedlist(movieId: Int? = null, title: String, posterPath: String, genre_ids: String, releaseDate: String, voteAverage: Double, isMovie: Boolean, tvId: Int? = null) {
        viewModelScope.launch {
            val watchedItem = WatchedItem(movieId = movieId, tvId = tvId, title = title, poster_path = posterPath, genre_ids = genre_ids, release_date = releaseDate, vote_average = voteAverage, isMovie = isMovie)
            db.watchedItemDao().addToWatched(watchedItem)
        }
    }

    fun removeFromWatchedMovielist(movieId: Int) {
        viewModelScope.launch {
            db.watchedItemDao().removeWatchedMovie(movieId)
        }
    }

    fun removeFromWatchedTvList(tvId: Int) {
        viewModelScope.launch {
            db.watchedItemDao().removeWatchedTvShow(tvId)
        }
    }

    private val _isWatchedItem = MutableLiveData<Boolean>()
    val isWatchedItem: LiveData<Boolean> = _isWatchedItem

    fun isWatchedItem(movieId: Int? = null, tvId: Int? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if(movieId != null){
                    val watchedItem = db.watchedItemDao().getWatchedMovie(movieId)
                    Log.e("RoomViewModel", "Watched Item: $watchedItem")
                    if(watchedItem != null) {
                        _isWatchedItem.postValue(true)
                    } else {
                        _isWatchedItem.postValue(false)
                    }
                } else if(tvId != null) {
                    val watchedItem = db.watchedItemDao().getWatchedTvvShow(tvId)
                    Log.e("RoomViewModel", "Watched Item: $watchedItem")
                    if(watchedItem != null) {
                        _isWatchedItem.postValue(true)
                    } else {
                        _isWatchedItem.postValue(false)
                    }
                }
            } catch (e: Exception) {
                Log.e("RoomViewModel", "Error: $e")
                _isWatchlistItem.postValue(false)
            }
        }
    }

}


