package com.example.whattowhat

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.whattowhat.model.Provider
import com.example.whattowhat.model.Rating

@Composable
fun MovieDetailsPage(movieId: String?, movieViewModel: MovieViewModel, navController: NavController, roomViewModel: RoomViewModel = viewModel()) {
    // Assuming movieDetails is a LiveData object that holds the details
    val movieDetails by movieViewModel.movieDetails.observeAsState(initial = null)
    val movieProviders by movieViewModel.movieProviders.observeAsState(initial = null)
    val movieRecommendations by movieViewModel.movieRecommendations.observeAsState(initial = null)
    val isWatchlistItem by roomViewModel.isWatchlistItem.observeAsState(initial = false)
    var recheckWatchlist by remember { mutableStateOf(false) }
    val isWatchedItem by roomViewModel.isWatchedItem.observeAsState(initial = false)
    var recheckWatched by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme
    var trailerContainerColor by remember { mutableStateOf(colorScheme.primary) }
    var trailerContentColor by remember { mutableStateOf(colorScheme.background) }
    var watchlistContainerColor by remember { mutableStateOf(colorScheme.primary) }
    var watchlistContentColor by remember { mutableStateOf(colorScheme.background) }
    var watchedContainerColor by remember { mutableStateOf(colorScheme.primary) }
    var watchedContentColor by remember { mutableStateOf(colorScheme.background) }
    var imdbRating by remember { mutableStateOf("0.0") }

    Log.e("MovieDetailsPage", "Is In Watchlist: $isWatchlistItem")

    val context = LocalContext.current

    LaunchedEffect(movieId) {
        movieViewModel.fetchMovieDetails(movieId!!.toInt(), "500f402322677a4df10fb559aa63f22b")
        movieViewModel.fetchMovieWatchProviders(movieId.toInt(), "500f402322677a4df10fb559aa63f22b")
        movieViewModel.getMovieRecommendations(movieId.toInt(), "500f402322677a4df10fb559aa63f22b")
        roomViewModel.isInWatchlist(movieId.toInt())

    }

    LaunchedEffect(movieDetails?.imdb_id) {
        movieDetails?.let {
            imdbRating = fetchMovieRating(it.imdb_id ?: "")
            Log.e("MovieDetailsPage", "IMDB Rating: $imdbRating")
        }
    }

    LaunchedEffect(recheckWatchlist) {
        roomViewModel.isInWatchlist(movieId!!.toInt())
    }

    LaunchedEffect(recheckWatched) {
        roomViewModel.isWatchedItem(movieId!!.toInt())
    }

    movieDetails?.let { movie ->
        Box (
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 11.dp, end = 0.dp)
        ){
            // Backdrop image
            BackdropImage(movie.backdrop_path)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.BottomStart)
            ) {
                // Title
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = "${movie.title} (${movie.release_date.take(4)})",
                        style = MaterialTheme.typography.displaySmall.copy(color = Color.White),
                        modifier = Modifier
                            .shadow(2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "IMDB Rating: ${imdbRating}",
                    style = MaterialTheme.typography.titleSmall.copy(color = Color.White),
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .shadow(2.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Rating
                Text(
                    text = "TMDB Rating: ${movie.vote_average}",
                    style = MaterialTheme.typography.labelLarge.copy(color = Color.White),
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .shadow(2.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Runtime
                Text(
                    text = movie.runtime.toString() + " minutes",
                    style = MaterialTheme.typography.labelLarge.copy(color = Color.White),
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .shadow(2.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Genres
                Text(
                    text = movie.genres.joinToString(", ") { it.name },
                    style = MaterialTheme.typography.labelLarge.copy(color = Color.White),
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .shadow(2.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Providers
                movieProviders?.let { providers ->
                    LazyRow {
                        items(providers.size) { index ->
                            ProviderLogo(providers[index])
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Overview
                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                    modifier = Modifier.shadow(2.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Play Trailer button
                Row (
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ){
                    Button(
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = trailerContainerColor,
                            contentColor = trailerContentColor
                        ),
                        onClick = {
                            movieViewModel.fetchTrailerMovie(
                                movie.id,
                                "500f402322677a4df10fb559aa63f22b"
                            )
                        },
                        modifier = Modifier
                            .onFocusChanged { focusState ->
                                trailerContainerColor = if (focusState.isFocused) colorScheme.onPrimaryContainer else colorScheme.primary
                                trailerContentColor = if (focusState.isFocused) colorScheme.background else colorScheme.background }
                    ) {
                        Text("Play Trailer")
                    }

                    val genreIds = movie.genres.joinToString(",") { it.id.toString() }
                    if(!isWatchlistItem){
                        Button(
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = watchlistContainerColor,
                                contentColor = watchlistContentColor
                            ),
                            onClick = {
                                roomViewModel.addToWatchlist(
                                    movie.id,
                                    movie.title,
                                    movie.poster_path,
                                    genreIds,
                                    movie.release_date,
                                    movie.vote_average
                                )
                                Toast.makeText(context, "Added to watchlist", Toast.LENGTH_SHORT).show()
                                recheckWatchlist = !recheckWatchlist
                            },
                            modifier = Modifier
                                .onFocusChanged { focusState ->
                                    watchlistContainerColor = if (focusState.isFocused) colorScheme.onPrimaryContainer else colorScheme.primary
                                    watchlistContentColor = if (focusState.isFocused) colorScheme.background else colorScheme.background }
                        ) {
                            Text("Add to Watchlist")
                        }
                    }else{
                        Button(
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = watchlistContainerColor,
                                contentColor = watchlistContentColor
                            ),
                            onClick = {
                                roomViewModel.removeFromWatchlist(movie.id)
                                Toast.makeText(context, "Removed from watchlist", Toast.LENGTH_SHORT).show()
                                recheckWatchlist = !recheckWatchlist
                            },
                            modifier = Modifier
                                .onFocusChanged { focusState ->
                                    watchlistContainerColor = if (focusState.isFocused) colorScheme.onPrimaryContainer else colorScheme.primary
                                    watchlistContentColor = if (focusState.isFocused) colorScheme.background else colorScheme.background }
                        ) {
                            Text("Remove from Watchlist")
                        }
                    }

                    if(!isWatchedItem) {
                        Button(
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = watchedContainerColor,
                                contentColor = watchedContentColor
                            ),
                            onClick = {
                                roomViewModel.addToWatchedlist(
                                    movie.id,
                                    movie.title,
                                    movie.poster_path,
                                    genreIds,
                                    movie.release_date,
                                    movie.vote_average,
                                    true
                                )
                                Toast.makeText(context, "Added to watched list", Toast.LENGTH_SHORT)
                                    .show()
                                recheckWatched = !recheckWatched
                            },
                            modifier = Modifier
                                .onFocusChanged { focusState ->
                                    watchedContainerColor = if (focusState.isFocused) colorScheme.onPrimaryContainer else colorScheme.primary
                                    watchedContentColor = if (focusState.isFocused) colorScheme.background else colorScheme.background }
                        ) {
                            Text("Mark as Watched")
                        }
                    }else{
                        Button(
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = watchedContainerColor,
                                contentColor = watchedContentColor
                            ),
                            onClick = {
                                roomViewModel.removeFromWatchedMovielist(movie.id)
                                Toast.makeText(context, "Removed from watched list", Toast.LENGTH_SHORT)
                                    .show()
                                recheckWatched = !recheckWatched
                            },
                            modifier = Modifier
                                .onFocusChanged { focusState ->
                                    watchedContainerColor = if (focusState.isFocused) colorScheme.onPrimaryContainer else colorScheme.primary
                                    watchedContentColor = if (focusState.isFocused) colorScheme.background else colorScheme.background }
                        ) {
                            Text("Unmark as Watched")
                        }
                    }
                }


                Spacer(modifier = Modifier.height(8.dp))
                // Movie Recommendations Section

                val movieRecommendationsFiltered = movieRecommendations?.filter { it.vote_count >= 250 }?.distinctBy { it.id }
                movieRecommendationsFiltered?.let { recommendations ->
                    Text(
                        text = "Recommendations",
                        style = MaterialTheme.typography.headlineMedium.copy(color = Color.White),
                        modifier = Modifier.padding(top = 0.dp, bottom = 4.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(recommendations.size) { index ->
                            val movieItem = recommendations[index]
                            RecommendationCard(movieItem, movieViewModel, navController)
                        }
                    }
                }

            }
        }
    } ?: Text(text = "Loading movie details...")

    // Observe the video ID LiveData.
    val videoIdEvent by movieViewModel.videoMovieId.observeAsState()
    videoIdEvent?.getContentIfNotHandled()?.let { videoId ->
        if (videoId.isNotEmpty()) {
            // When the video ID is available, launch the YouTubePlayerActivity with the ID.
            val intent = Intent(context, YouTubePlayerActivity::class.java)
            intent.putExtra("VIDEO_ID", videoId)
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Video not available", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun ProviderLogo(provider: Provider) {
    val imageUrl = "https://image.tmdb.org/t/p/original${provider.logo_path}"
    Image(
        painter = rememberAsyncImagePainter(imageUrl),
        contentDescription = "Provider Logo",
        modifier = Modifier
            .size(50.dp) // You can adjust the size as needed
            .padding(4.dp)
            .clip(RoundedCornerShape(20))
    )
}

@Composable
fun RecommendationImage(backdropPath: String?) {
    val imageUrlBase = "https://image.tmdb.org/t/p/original"
    backdropPath?.let {
        val imageUrl = "$imageUrlBase$it"
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = "Backdrop Image",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight() // Adjust height accordingly
                .clip(RoundedCornerShape(20)),
            contentScale = ContentScale.Crop
        )
        // Dark gradient overlay if needed
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(20))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black),
                        startY = 0f // Adjust gradient to your liking
                    )
                )
        )
    }
}

@Composable
fun BackdropImage(backdropPath: String?) {
    val imageUrlBase = "https://image.tmdb.org/t/p/original"
    backdropPath?.let {
        val imageUrl = "$imageUrlBase$it"
        Log.e("MovieViewModel", "IMAGE URL: $imageUrl")
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = "Backdrop Image",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(), // Adjust height accordingly
            contentScale = ContentScale.Crop
        )
        // Dark gradient overlay if needed
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black),
                        startY = 0f // Adjust gradient to your liking
                    )
                )
        )
    }
}

suspend fun fetchMovieRating(imdbId: String): String {
    val response = RetrofitClient.omdbInstance.getMovieRatingFromOMDb(imdbId)
    return if (response.isSuccessful) {
        // Successfully fetched the rating
        response.body()?.imdbRating ?: "Rating not available"
    } else {
        // Handle error scenario
        println("Error fetching rating: ${response.errorBody()}")
        "Error fetching rating"
    }
}
