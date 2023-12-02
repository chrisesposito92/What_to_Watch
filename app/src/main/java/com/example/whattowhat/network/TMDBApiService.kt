import com.example.whattowhat.model.MovieDetail
import com.example.whattowhat.model.MovieDiscoverResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.whattowhat.model.TvDiscoverResponse
import com.example.whattowhat.model.ProviderResponse
import com.example.whattowhat.model.MovieVideosResponse
import com.example.whattowhat.model.MovieResponse
import com.example.whattowhat.model.OMDbMovieResponse
import com.example.whattowhat.model.TvVideosResponse
import retrofit2.Response
import retrofit2.http.Path
import com.example.whattowhat.model.ProviderResponseMovie

interface TMDBApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("api_key") apiKey: String): Response<MovieResponse>

    @GET("watch/providers/movie?language=en-US&watch_region=US")
    suspend fun getWatchProvidersMovies(@Query("api_key") apiKey: String): Response<ProviderResponse>

    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("include_video") includeVideo: Boolean = false,
        @Query("page") page: Int = 1,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("watch_region") watchRegion: String = "US",
        @Query("with_watch_providers") providerId: String? = null,
        @Query("with_genres") genreId: String? = null,
        @Query("without_genres") withoutGenreId: String? = null,
        @Query("with_original_language") originalLanguage: String = "en",
        @Query("vote_count.gte") voteCount: Int? = null,
        @Query("vote_average.gte") voteAverage: Int? = null,
        @Query("primary_release_year") primaryReleaseYear: Int? = null,
    ): Response<MovieDiscoverResponse>

    @GET("discover/tv")
    suspend fun discoverTV(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("include_null_first_air_dates") includeNullAirDate: Boolean = false,
        @Query("page") page: Int = 1,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("watch_region") watchRegion: String = "US",
        @Query("with_watch_providers") providerId: String? = null,
        @Query("with_genres") genreId: String? = null,
        @Query("without_genres") withoutGenreId: String? = null,
        @Query("with_original_language") originalLanguage: String = "en",
        @Query("vote_count.gte") voteCount: Int? = 250,
        @Query("vote_average.gte") voteAverage: Int? = null,
        @Query("first_air_date_year") firstAirDateYear: Int? = null,
    ): Response<TvDiscoverResponse>

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US"
    ): Response<MovieVideosResponse>

    @GET("tv/{series_id}/videos")
    suspend fun getTvVideos(
        @Path("series_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US"
    ): Response<TvVideosResponse>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US"
    ): Response<MovieDetail>

    @GET("movie/{movie_id}/watch/providers")
    suspend fun getMovieWatchProviders(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Response<ProviderResponseMovie>

    @GET("movie/{movie_id}/recommendations")
    suspend fun getMovieRecommendations(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Response<MovieDiscoverResponse>

}
