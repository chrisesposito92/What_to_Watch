package com.example.whattowhat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.whattowhat.model.MovieItem
import kotlinx.coroutines.Dispatchers

class MovieViewModel : ViewModel() {
    fun getMovieProviders(apiKey: String) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitClient.instance.getWatchProvidersMovies(apiKey).execute()
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

    fun getMoviesByProvider(apiKey: String, providerId: String) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitClient.instance.discoverMovies(apiKey, providerId = providerId)
            if (response.isSuccessful) {
                emit(response.body()?.results ?: emptyList())
            } else {
                Log.e("MovieViewModel", "Error fetching movies: ${response.errorBody()?.string()}")
                emit(emptyList())
            }
        } catch (e: Exception) {
            Log.e("MovieViewModel", "Exception fetching movies", e)
            emit(emptyList())
        }
    }

}

