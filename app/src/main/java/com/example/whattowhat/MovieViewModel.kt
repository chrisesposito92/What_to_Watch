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

    // Method to fetch movies by provider and genre
    fun getMoviesByProviderAndGenre(apiKey: String, providerId: String, genreId: String, page: Int, sortBy: String, excludeAnimation: Boolean) {
        viewModelScope.launch {
            val withoutGenreId = if (excludeAnimation) "16" else null
            val response = RetrofitClient.instance.discoverMovies(
                apiKey = apiKey,
                providerId = providerId,
                genreId = genreId,
                page = page,
                sortBy = sortBy,
                withoutGenreId = withoutGenreId
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

    fun getMoviesByProvider(apiKey: String, providerId: String, page: Int, sortBy: String, excludeAnimation: Boolean) {
        viewModelScope.launch {
            val withoutGenreId = if (excludeAnimation) "16" else null
            val response = RetrofitClient.instance.discoverMovies(
                apiKey = apiKey,
                providerId = providerId,
                page = page,
                sortBy = sortBy,
                withoutGenreId = withoutGenreId
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

    fun getMoviesByGenre(apiKey: String, genreId: String, page: Int, sortBy: String, excludeAnimation: Boolean) {
        viewModelScope.launch {
            val withoutGenreId = if (excludeAnimation) "16" else null
            val response = RetrofitClient.instance.discoverMovies(
                apiKey = apiKey,
                genreId = genreId,
                page = page,
                sortBy = sortBy,
                withoutGenreId = withoutGenreId
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

    fun getMovies(apiKey: String, page: Int, sortBy: String, excludeAnimation: Boolean) {
        viewModelScope.launch {
            val withoutGenreId = if (excludeAnimation) "16" else null
            val response = RetrofitClient.instance.discoverMovies(
                apiKey = apiKey,
                page = page,
                sortBy = sortBy,
                withoutGenreId = withoutGenreId
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

    private val _trailerUrl = MutableLiveData<Event<String?>>()
    val trailerUrl: LiveData<Event<String?>> = _trailerUrl

    fun fetchTrailer(movieId: Int, apiKey: String) {
        viewModelScope.launch(Dispatchers.IO) {  // Switch to IO Dispatcher
            try {
                val response = RetrofitClient.instance.getMovieVideos(movieId, apiKey)
                if (response.isSuccessful) {
                    val trailers = response.body()?.results?.filter { it.isYoutubeTrailer() }
                    trailers?.firstOrNull()?.let {
                        val trailerUrl = "https://www.youtube.com/watch?v=${it.key}"
                        Log.d("MovieViewModel", "Trailer URL: $trailerUrl") // Log the trailer URL
                        _trailerUrl.postValue(Event("https://www.youtube.com/watch?v=${it.key}"))
                    } ?: run {
                        Log.e("MovieViewModel", "No trailers found.")
                    }
                } else {
                    Log.e("MovieViewModel", "Error fetching trailer: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Exception fetching trailer", e)
            }
        }
    }

    // Method to clear the trailer URL
    fun clearTrailerUrl() {
        _trailerUrl.postValue(null)
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

