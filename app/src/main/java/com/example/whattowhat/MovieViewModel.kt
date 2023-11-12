package com.example.whattowhat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers

class MovieViewModel : ViewModel() {

    fun getPopularMovies(apiKey: String) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitClient.instance.getPopularMovies(apiKey).execute()
            if (response.isSuccessful) {
                // Log the successful response
                Log.d("MovieViewModel", "Fetched movies: ${response.body()?.results}")
                emit(response.body()?.results ?: emptyList())
            } else {
                // Log the error response
                Log.e("MovieViewModel", "Error fetching movies: ${response.errorBody()?.string()}")
                emit(emptyList()) // Make sure to emit an empty list or some error state
            }
        } catch (e: Exception) {
            // Log the exception
            Log.e("MovieViewModel", "Exception fetching movies", e)
            emit(emptyList()) // Make sure to emit an empty list or some error state
        }
    }

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
}

