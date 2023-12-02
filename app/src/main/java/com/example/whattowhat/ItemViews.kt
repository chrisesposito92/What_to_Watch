package com.example.whattowhat

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.whattowhat.model.MovieGenreData
import com.example.whattowhat.model.MovieItem
import com.example.whattowhat.model.TvGenreData
import com.example.whattowhat.model.TvItem

@Composable
fun MovieItemView(movie: MovieItem, movieViewModel: MovieViewModel, navController: NavController) {
    val imageUrlBase = "https://image.tmdb.org/t/p/w780"
    val genres = movie.genre_ids.joinToString(", ") { genreId ->
        MovieGenreData.genres.first { it.id == genreId }.name
    }
    val colorScheme = MaterialTheme.colorScheme
    var color by remember { mutableStateOf(colorScheme.background) }
    var borderSize by remember { mutableStateOf(0.dp) }
    Card(
        shape = RoundedCornerShape(5),
        modifier = Modifier
            .padding(0.dp)
            .onFocusChanged { focusState ->
                color = if (focusState.isFocused) colorScheme.onPrimaryContainer else colorScheme.background
                borderSize = if (focusState.isFocused) 3.dp else 0.dp
            }
            .border(borderSize, color, shape = RoundedCornerShape(5))
            .clickable {
                navController.navigate("movieDetail/${movie.id}")
            }
    ) {
        movie.poster_path?.let { posterPath ->
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
            text = movie.title + " (" + movie.release_date.substring(0, 4) + ")",
            style = TextStyle(
                fontSize = 12.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(start = 8.dp, top = 5.dp, end = 8.dp, bottom = 1.dp)
        )
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 2.dp, end = 10.dp, bottom = 0.dp)
        ){
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Rating",
                tint = Color.Yellow,
                modifier = Modifier
                    .size(10.dp)
            )
            Text(
                text = "${movie.vote_average}",
                style = TextStyle(
                    fontSize = 10.sp,
                    color = Color.Black
                )
            )
        }
        Text(
            text = genres.substring(0, genres.length),
            style = TextStyle(
                fontSize = 10.sp,
                color = Color.Black
            ),
            modifier = Modifier.padding(start = 10.dp, top = 0.dp, end = 10.dp, bottom = 10.dp)
        )

    }
}

@Composable
fun TvItemView(tv: TvItem, movieViewModel: MovieViewModel, navController: NavController ) {
    val context = LocalContext.current
    val imageUrlBase = "https://image.tmdb.org/t/p/w185"
    val genres = tv.genre_ids.joinToString(", ") { genreId ->
        TvGenreData.genres.first { it.id == genreId }.name
    }
    var color by remember { mutableStateOf(Color.White) }

    Card(
        modifier = Modifier
            .padding(5.dp)
            // Apply onFocusChanged modifier directly to Card
            .onFocusChanged { focusState ->
                color = if (focusState.isFocused) Color.Magenta else Color.White
            }
            .border(2.dp, color, shape = RoundedCornerShape(10))
            .clickable {
                //    movieViewModel.fetchTrailerTv(tv.id, "500f402322677a4df10fb559aa63f22b")
                navController.navigate("tvDetail/${tv.id}")
            },
    ) {

        tv.poster_path?.let { posterPath ->
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
            text = tv.name + " (" + tv.first_air_date.substring(0, 4) + ")",
            style = TextStyle(
                fontSize = 12.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(start = 8.dp, top = 5.dp, end = 8.dp, bottom = 1.dp)
        )
        Text(
            text = "Rating: ${tv.vote_average}",
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
    val videoIdEvent by movieViewModel.videoTvId.observeAsState()
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

