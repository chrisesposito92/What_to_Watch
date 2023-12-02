package com.example.whattowhat

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.whattowhat.model.MovieGenreData
import com.example.whattowhat.model.WatchlistItem


@Composable
fun WatchlistScreen(roomViewModel: RoomViewModel = viewModel(), navController: NavController) {
    val watchlist by roomViewModel.watchlist.observeAsState(initial = emptyList())
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 15.dp, top = 0.dp, end = 0.dp, bottom = 0.dp)
    ) {
        Text("Watchlist", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            state = rememberLazyGridState(),
            columns = GridCells.Fixed(8)
        ) {
            items(watchlist.size) { index ->
                val watchlist = watchlist[index]
                WatchlistItemView(watchlist, viewModel(), navController,roomViewModel)
            }
        }
    }
}

@Composable
fun WatchlistItemView(watchlist: WatchlistItem, movieViewModel: MovieViewModel, navController: NavController, roomViewModel: RoomViewModel ){
    val context = LocalContext.current
    val imageUrlBase = "https://image.tmdb.org/t/p/w780"
    val genres = watchlist.genre_ids.split(",").joinToString(", ") { genreId ->
        MovieGenreData.genres.first { it.id == genreId.toInt() }.name
    }
    var color by remember { mutableStateOf(Color.White) }
    Card(
        modifier = Modifier
            .padding(0.dp)
            // Apply onFocusChanged modifier directly to Card
            .onFocusChanged { focusState ->
                color = if (focusState.isFocused) Color.Magenta else Color.White
            }
            .border(2.dp, color, shape = RoundedCornerShape(10))
            .clickable {
                //    movieViewModel.fetchTrailerMovie(movie.id, "500f402322677a4df10fb559aa63f22b")
                Log.e("MovieViewModel", "MOVIE ID: ${watchlist.movieId}")
                navController.navigate("movieDetail/${watchlist.movieId}")
            },
    ) {
        watchlist.poster_path.let { posterPath ->
            val imageUrl = "$imageUrlBase$posterPath"
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Movie Poster",
                modifier = Modifier
                    .aspectRatio(2 / 3f)
                    .fillMaxWidth(),
                contentScale = ContentScale.FillHeight
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = watchlist.title + " (" + watchlist.release_date.substring(0, 4) + ")",
            style = TextStyle(
                fontSize = 12.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(start = 8.dp, top = 5.dp, end = 8.dp, bottom = 1.dp)
        )
        Text(
            text = "Rating: ${watchlist.vote_average}",
            style = TextStyle(
                fontSize = 10.sp,
                color = Color.Black
            ),
            modifier = Modifier.padding(start = 10.dp, top = 2.dp, end = 10.dp, bottom = 0.dp)
        )
        Text(
            text = genres.substring(0, genres.length),
            style = TextStyle(
                fontSize = 10.sp, // Set the font size to 12 sp for example
                color = Color.Black // Optional: if you want to change the color
            ),
            modifier = Modifier.padding(start = 10.dp, top = 0.dp, end = 10.dp, bottom = 10.dp)
        )
    }

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