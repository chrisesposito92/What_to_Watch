package com.example.whattowhat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.whattowhat.model.MovieItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {
    fun getMovieProviders(apiKey: String) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitClient.instance.getWatchProvidersMovies(apiKey);
            if (response.isSuccessful) {
                emit(response.body()?.results ?: emptyList())
            } else {
                Log.e("MovieViewModel", "Error fetching providers: ${response.errorBody()?.string()}")
                emit(emptyList())
            }
        } catch (e: Exception) {
            Log.e("MovieViewModel", "Exception fetching providers", e)
            emit(emptyList())
        }
    }

    // LiveData to observe movies
    private val _moviesState = MutableLiveData<List<MovieItem>>()
    val moviesState: LiveData<List<MovieItem>> = _moviesState

    private val _totalPages = MutableLiveData<Int>()
    val totalPages: LiveData<Int> = _totalPages

    fun getMovies(apiKey: String, page: Int, sortBy: String, excludeAnimation: Boolean, providerId: String? = null, genreId: String? = null, year: Int? = null, voteCount: Int? = null) {
        viewModelScope.launch {
            val withoutGenreId = if (excludeAnimation) "16" else null
            val response = RetrofitClient.instance.discoverMovies(
                apiKey = apiKey,
                providerId = providerId,
                genreId = genreId,
                page = page,
                sortBy = sortBy,
                voteCount = voteCount,
                withoutGenreId = withoutGenreId,
                primaryReleaseYear = year
            )
            if (response.isSuccessful) {
                _moviesState.postValue(response.body()?.results)
                _totalPages.postValue(response.body()?.total_pages)
            } else {
                Log.e("MovieViewModel", "Error fetching movies: ${response.errorBody()?.string()}")
                _moviesState.postValue(emptyList())
            }
        }
    }

    private val _videoId = MutableLiveData<Event<String?>>()
    val videoId: LiveData<Event<String?>> = _videoId

    fun fetchTrailer(movieId: Int, apiKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getMovieVideos(movieId, apiKey)
                if (response.isSuccessful) {
                    // Filter for YouTube trailers
                    val trailers = response.body()?.results?.filter { it.isYoutubeTrailer() }
                    // Get the first YouTube trailer, if available
                    trailers?.firstOrNull()?.let {
                        // Instead of constructing the full URL, just post the video ID
                        _videoId.postValue(Event(it.key))
                    } ?: run {
                        Log.e("MovieViewModel", "No trailers found.")
                        _videoId.postValue(Event(null)) // Post null if no trailer found
                    }
                } else {
                    Log.e("MovieViewModel", "Error fetching trailer: ${response.errorBody()?.string()}")
                    _videoId.postValue(Event(null)) // Post null on error
                }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Exception fetching trailer", e)
                _videoId.postValue(Event(null)) // Post null on exception
            }
        }
    }



    open class Event<out T>(private val content: T) {
        var hasBeenHandled = false
            private set

        fun getContentIfNotHandled(): T? {
            if (hasBeenHandled) {
                return null
            } else {
                hasBeenHandled = true
                return content
            }
        }

        fun peekContent(): T = content
    }


}

