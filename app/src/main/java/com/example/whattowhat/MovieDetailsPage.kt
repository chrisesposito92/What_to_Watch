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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.whattowhat.model.Provider

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
    val selectedProviderIdList = RememberProviders().getSelectedProviders(LocalContext.current).map { it }
    val selectedProviderIdString = selectedProviderIdList.joinToString(",")

    Log.e("MovieDetailsPage", "Is In Watchlist: ${isWatchlistItem}")

    val context = LocalContext.current

    LaunchedEffect(movieId) {
        movieViewModel.fetchMovieDetails(movieId!!.toInt(), "500f402322677a4df10fb559aa63f22b")
        movieViewModel.fetchMovieWatchProviders(movieId.toInt(), "500f402322677a4df10fb559aa63f22b")
        movieViewModel.getMovieRecommendations(movieId.toInt(), "500f402322677a4df10fb559aa63f22b")
        roomViewModel.isInWatchlist(movieId.toInt())
    }

    LaunchedEffect(recheckWatchlist) {
        roomViewModel.isInWatchlist(movieId!!.toInt())
    }

    LaunchedEffect(recheckWatched) {
        roomViewModel.isWatchedItem(movieId!!.toInt())
    }

    movieDetails?.let { movie ->
        Box {
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
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = "${movie.title} (${movie.release_date.take(4)})",
                        style = MaterialTheme.typography.displaySmall.copy(color = Color.White),
                        modifier = Modifier
                            .shadow(2.dp)
                            .clickable {}
                    )
                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = {
                            navController.navigate("movietvList/${selectedProviderIdString}"){
                                popUpTo("providerSelection") { inclusive = true }
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Text("Home")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Rating
                Row {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color.Yellow,
                        modifier = Modifier
                            .size(16.dp)
                    )
                    Text(
                        text = "${movie.vote_average} / 10",
                        style = MaterialTheme.typography.labelLarge.copy(color = Color.White),
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .shadow(2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Runtime
                Text(
                    text = movie.runtime.toString() + " minutes",
                    style = MaterialTheme.typography.labelLarge.copy(color = Color.White),
                    modifier = Modifier.shadow(2.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Genres
                Text(
                    text = movie.genres.joinToString(", ") { it.name },
                    style = MaterialTheme.typography.labelLarge.copy(color = Color.White),
                    modifier = Modifier.shadow(2.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Providers
                movieProviders?.let { providers ->
                    LazyRow {
                        items(providers.size) { index ->
                            ProviderLogo(providers[index])
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Overview
                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                    modifier = Modifier.shadow(2.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
                // Play Trailer button
                Row (
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ){
                    Button(
                        onClick = {
                            movieViewModel.fetchTrailerMovie(
                                movie.id,
                                "500f402322677a4df10fb559aa63f22b"
                            )
                        }
                    ) {
                        Text("Play Trailer")
                    }

                    val genreIds = movie.genres.joinToString(",") { it.id.toString() }
                    if(isWatchlistItem == null || isWatchlistItem == false){
                        Button(
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
                            }
                        ) {
                            Text("Add to Watchlist")
                        }
                    }else{
                        Button(
                            onClick = {
                                roomViewModel.removeFromWatchlist(movie.id)
                                Toast.makeText(context, "Removed from watchlist", Toast.LENGTH_SHORT).show()
                                recheckWatchlist = !recheckWatchlist
                            }
                        ) {
                            Text("Remove from Watchlist")
                        }
                    }

                    if(isWatchedItem == null || isWatchedItem == false) {
                        Button(
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
                            }
                        ) {
                            Text("Mark as Watched")
                        }
                    }else{
                        Button(
                            onClick = {
                                roomViewModel.removeFromWatchedMovielist(movie.id)
                                Toast.makeText(context, "Removed from watched list", Toast.LENGTH_SHORT)
                                    .show()
                                recheckWatched = !recheckWatched
                            }
                        ) {
                            Text("Unmark as Watched")
                        }
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))
                // Movie Recommendations Section

                val movieRecommendationsFiltered = movieRecommendations?.filter { it.vote_count >= 250 }?.distinctBy { it.id }
                movieRecommendationsFiltered?.let { recommendations ->
                    Text(
                        text = "Recommendations",
                        style = MaterialTheme.typography.headlineMedium.copy(color = Color.White),
                        modifier = Modifier.padding(top = 0.dp, bottom = 8.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(recommendations.size) { index ->
                            val movie = recommendations[index]
                            RecommendationCard(movie, movieViewModel, navController)
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