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

    // Method to fetch movies by provider and genre
    fun getMoviesByProviderAndGenre(apiKey: String, providerId: String, genreId: String) {
        viewModelScope.launch {
            // Fetch movies from the repository or use Retrofit to make the network call
            // Update the _moviesState LiveData
            // For example:
            val response = RetrofitClient.instance.discoverMovies(
                apiKey = apiKey,
                providerId = providerId,
                genreId = genreId
            )
            if (response.isSuccessful) {
                _moviesState.postValue(response.body()?.results)
            } else {
                Log.e("MovieViewModel", "Error fetching movies: ${response.errorBody()?.string()}")
                _moviesState.postValue(emptyList())
            }
        }
    }

}

