import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.whattowhat.model.MovieResponse
import com.example.whattowhat.model.ProviderResponse

interface TMDBApiService {
    @GET("movie/popular")
    fun getPopularMovies(@Query("api_key") apiKey: String): Call<MovieResponse>

    @GET("watch/providers/movie?language=en-US&watch_region=US")
    fun getWatchProvidersMovies(@Query("api_key") apiKey: String): Call<ProviderResponse>

    // Add more API methods as needed
}
