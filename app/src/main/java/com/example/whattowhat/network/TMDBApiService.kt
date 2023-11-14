import com.example.whattowhat.model.MovieDiscoverResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.whattowhat.model.MovieResponse
import com.example.whattowhat.model.ProviderResponse
import retrofit2.Response

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
        @Query("watch_region") watchRegion: String = "US",
        @Query("with_watch_providers") providerId: String,
        @Query("with_genres") genreId: String
    ): Response<MovieDiscoverResponse>
}
