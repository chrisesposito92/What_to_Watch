package com.example.whattowhat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whattowhat.model.MovieDetail
import com.example.whattowhat.model.MovieItem
import com.example.whattowhat.model.Provider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.whattowhat.model.TvItem

class MovieViewModel : ViewModel() {

    private val _moviesSearchState = MutableLiveData<List<MovieItem>>()
    val moviesSearchState: LiveData<List<MovieItem>> = _moviesSearchState

    fun searchMovies(apiKey: String, query: String) {
        viewModelScope.launch {
            val response = RetrofitClient.tmdbInstance.searchMovies(
                apiKey = apiKey,
                page = 1,
                query = query
            )

            var movies = mutableListOf<MovieItem>()

            if(response.isSuccessful) {
                val totalPages = response.body()!!.total_pages
                for(Int in 1..totalPages){
                    val response = RetrofitClient.tmdbInstance.searchMovies(
                        apiKey = apiKey,
                        page = Int,
                        query = query
                    )
                    if(response.isSuccessful){
                        if(Int == 1){
                            movies = response.body()!!.results.toMutableList()
                        }else{
                            movies = (movies + response.body()!!.results.toMutableList()).toMutableList()
                        }
                    } else {
                        Log.e("MovieViewModel", "Error fetching movies: ${response.errorBody()?.string()}")
                    }
                }
                _moviesSearchState.postValue(movies)
            }else {
                Log.e("MovieViewModel", "Error fetching movies: ${response.errorBody()?.string()}")
            }
        }
    }

    // LiveData to observe movies
    private val _moviesState = MutableLiveData<List<MovieItem>>()
    val moviesState: LiveData<List<MovieItem>> = _moviesState

    private val _totalPagesMovie = MutableLiveData<Int>()
    val totalPagesMovie: LiveData<Int> = _totalPagesMovie

    fun getMovies(apiKey: String, page: Int, sortBy: String, excludeAnimation: Boolean, providerId: String? = null, genreId: String? = null, year: Int? = null, voteCount: Int? = 250, voteAverage: Int? = null) {
        viewModelScope.launch {
            val withoutGenreId = if (excludeAnimation) "16" else null
            val apiPage1 = 2 * page -1
            val apiPage2 = 2 * page
            val response1 = RetrofitClient.tmdbInstance.discoverMovies(
                apiKey = apiKey,
                providerId = providerId,
                genreId = genreId,
                page = apiPage1,
                sortBy = sortBy,
                voteCount = voteCount,
                voteAverage = voteAverage,
                withoutGenreId = withoutGenreId,
                primaryReleaseYear = year
            )

            if(response1.isSuccessful && response1.body()!!.results.size <= 20){


                val response2 = RetrofitClient.tmdbInstance.discoverMovies(
                    apiKey = apiKey,
                    providerId = providerId,
                    genreId = genreId,
                    page = apiPage2,
                    sortBy = sortBy,
                    voteCount = voteCount,
                    voteAverage = voteAverage,
                    withoutGenreId = withoutGenreId,
                    primaryReleaseYear = year
                )
                if(response2.isSuccessful && response2.body()!!.results.isNotEmpty()){
                    val movies = response1.body()!!.results + response2.body()!!.results
                    _moviesState.postValue(movies)
                    val totalPages = kotlin.math.ceil(response1.body()!!.total_pages.toDouble() / 2).toInt()
                    _totalPagesMovie.postValue(totalPages)
                } else {
                    val movies = response1.body()!!.results
                    _moviesState.postValue(movies)
                    val totalPages = kotlin.math.ceil(response1.body()!!.total_pages.toDouble() / 2).toInt()
                    _totalPagesMovie.postValue(totalPages)
                }
            } else {
                Log.e("MovieViewModel", "Error fetching movies1: ${response1.errorBody()?.string()}")
            }

        }
    }

    private val _tvState = MutableLiveData<List<TvItem>>()
    val tvState: LiveData<List<TvItem>> = _tvState

    private val _totalPagesTv = MutableLiveData<Int>()
    val totalPagesTv: LiveData<Int> = _totalPagesTv

    fun getTV(apiKey: String, page: Int, sortBy: String, excludeAnimation: Boolean, providerId: String? = null, genreId: String? = null, year: Int? = null, voteCount: Int? = 250, voteAverage: Int? = null) {
        viewModelScope.launch {
            val withoutGenreId = if (excludeAnimation) "16" else null
            val apiPage1 = 2 * page -1
            val apiPage2 = 2 * page
            val response1 = RetrofitClient.tmdbInstance.discoverTV(
                apiKey = apiKey,
                providerId = providerId,
                genreId = genreId,
                page = apiPage1,
                sortBy = sortBy,
                voteCount = voteCount,
                voteAverage = voteAverage,
                withoutGenreId = withoutGenreId,
                firstAirDateYear = year
            )

            if(response1.isSuccessful && response1.body()!!.results.size <= 20){


                val response2 = RetrofitClient.tmdbInstance.discoverTV(
                    apiKey = apiKey,
                    providerId = providerId,
                    genreId = genreId,
                    page = apiPage2,
                    sortBy = sortBy,
                    voteCount = voteCount,
                    voteAverage = voteAverage,
                    withoutGenreId = withoutGenreId,
                    firstAirDateYear = year
                )
                if(response2.isSuccessful && response2.body()!!.results.isNotEmpty()){
                    val tv = response1.body()!!.results + response2.body()!!.results
                    _tvState.postValue(tv)
                    val totalPages = kotlin.math.ceil(response1.body()!!.total_pages.toDouble() / 2).toInt()
                    _totalPagesTv.postValue(totalPages)
                } else {
                    val tv = response1.body()!!.results
                    _tvState.postValue(tv)
                    val totalPages = kotlin.math.ceil(response1.body()!!.total_pages.toDouble() / 2).toInt()
                    _totalPagesTv.postValue(totalPages)
                }
            } else {
                Log.e("MovieViewModel", "Error fetching tv shows: ${response1.errorBody()?.string()}")
            }
        }
    }

    private val _videoMovieId = MutableLiveData<Event<String?>>()
    val videoMovieId: LiveData<Event<String?>> = _videoMovieId

    fun fetchTrailerMovie(movieId: Int, apiKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.tmdbInstance.getMovieVideos(movieId, apiKey)
                if (response.isSuccessful) {
                    // Filter for YouTube trailers
                    val trailers = response.body()?.results?.filter { it.isYoutubeTrailer() }
                    // Get the first YouTube trailer, if available
                    trailers?.firstOrNull()?.let {
                        // Instead of constructing the full URL, just post the video ID
                        _videoMovieId.postValue(Event(it.key))
                    } ?: run {
                        Log.e("MovieViewModel", "No trailers found.")
                        _videoMovieId.postValue(Event(null)) // Post null if no trailer found
                    }
                } else {
                    Log.e("MovieViewModel", "Error fetching trailer: ${response.errorBody()?.string()}")
                    _videoMovieId.postValue(Event(null)) // Post null on error
                }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Exception fetching trailer", e)
                _videoMovieId.postValue(Event(null)) // Post null on exception
            }
        }
    }

    private val _videoTvId = MutableLiveData<Event<String?>>()
    val videoTvId: LiveData<Event<String?>> = _videoTvId

    fun fetchTrailerTv(tvId: Int, apiKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.tmdbInstance.getTvVideos(tvId, apiKey)
                if (response.isSuccessful) {
                    // Filter for YouTube trailers
                    val trailers = response.body()?.results?.filter { it.isYoutubeTrailer() }
                    // Get the first YouTube trailer, if available
                    trailers?.firstOrNull()?.let {
                        // Instead of constructing the full URL, just post the video ID
                        _videoTvId.postValue(Event(it.key))
                    } ?: run {
                        Log.e("MovieViewModel", "No trailers found.")
                        _videoTvId.postValue(Event(null)) // Post null if no trailer found
                    }
                } else {
                    Log.e("MovieViewModel", "Error fetching trailer: ${response.errorBody()?.string()}")
                    _videoTvId.postValue(Event(null)) // Post null on error
                }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Exception fetching trailer", e)
                _videoTvId.postValue(Event(null)) // Post null on exception
            }
        }
    }

    open class Event<out T>(private val content: T) {
        private var hasBeenHandled = false

        fun getContentIfNotHandled(): T? {
            return if (hasBeenHandled) {
                null
            } else {
                hasBeenHandled = true
                content
            }
        }

        fun peekContent(): T = content
    }

    private val _movieDetails = MutableLiveData<MovieDetail>()
    val movieDetails: LiveData<MovieDetail> = _movieDetails

    fun fetchMovieDetails(movieId: Int, apiKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.tmdbInstance.getMovieDetails(movieId, apiKey)
                if (response.isSuccessful) {
                    _movieDetails.postValue(response.body())
                } else {
                    Log.e("MovieViewModel", "Error fetching movie details: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Exception fetching movie details", e)
            }
        }
    }

    private val _movieProviders = MutableLiveData<List<Provider>>()
    val movieProviders: LiveData<List<Provider>> = _movieProviders

    fun fetchMovieWatchProviders(movieId: Int, apiKey: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.tmdbInstance.getMovieWatchProviders(movieId, apiKey)

                Log.d("MovieViewModel", "fetchMovieWatchProviders: $response")
                Log.d("MovieViewModel", "fetchMovieWatchProviders: ${response.body()}")
                if (response.isSuccessful) {
                    val usProvidersBuy = response.body()?.results?.get("US")?.buy ?: emptyList()
                    val usProvidersRent = response.body()?.results?.get("US")?.rent ?: emptyList()
                    val usProvidersFlat = response.body()?.results?.get("US")?.flatrate ?: emptyList()
                    val usProviders = (usProvidersBuy + usProvidersRent + usProvidersFlat).distinct().sortedBy { it.display_priority }

                    _movieProviders.postValue(usProviders)
                } else {
                    Log.e("MovieViewModel", "Error fetching movie providers: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Exception fetching movie providers", e)
            }
        }
    }

    // LiveData to observe movies
    private val _movieRecommendations = MutableLiveData<List<MovieItem>>()
    val movieRecommendations: LiveData<List<MovieItem>> = _movieRecommendations

    fun getMovieRecommendations(movieId: Int, apiKey: String) {
        viewModelScope.launch {
            val response1 = RetrofitClient.tmdbInstance.getMovieRecommendations(
                movieId = movieId,
                apiKey = apiKey,
                page = 1
            )

            if(response1.isSuccessful){

                val response2 = RetrofitClient.tmdbInstance.getMovieRecommendations(
                    movieId = movieId,
                    apiKey = apiKey,
                    page = 2
                )
                if(response2.isSuccessful){
                    val movies = response1.body()!!.results + response2.body()!!.results
                    _movieRecommendations.postValue(movies)

                } else {
                    val movies = response1.body()!!.results
                    _movieRecommendations.postValue(movies)

                }
            } else {
                Log.e("MovieViewModel", "Error fetching movies1: ${response1.errorBody()?.string()}")
            }

        }
    }
}

