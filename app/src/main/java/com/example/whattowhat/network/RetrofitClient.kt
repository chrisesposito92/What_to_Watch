import com.example.whattowhat.network.OMDBApiService
import com.example.whattowhat.network.OpenAIService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
    private const val OMDB_BASE_URL = "http://www.omdbapi.com/"

    val tmdbInstance: TMDBApiService by lazy {
        Retrofit.Builder()
            .baseUrl(TMDB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TMDBApiService::class.java)
    }

    val omdbInstance: OMDBApiService by lazy {
        Retrofit.Builder()
            .baseUrl(OMDB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OMDBApiService::class.java)
    }

    private const val OPENAI_BASE_URL = "https://api.openai.com/"

    val openAiInstance: OpenAIService by lazy {
        Retrofit.Builder()
            .baseUrl(OPENAI_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAIService::class.java)
    }
}
