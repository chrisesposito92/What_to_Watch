package com.example.whattowhat

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.whattowhat.model.MovieItem

@Composable
fun RecommendationCard(movie: MovieItem, movieViewModel: MovieViewModel = viewModel(), navController: NavController) {
    var color by remember { mutableStateOf(Color.White) }
    Box(
        modifier = Modifier
            .width(150.dp)
            .height(250.dp)
            .padding(4.dp)
            .onFocusChanged { focusState ->
                color = if (focusState.isFocused) Color.Magenta else Color.White
            }
            .clickable {
                //    movieViewModel.fetchTrailerMovie(movie.id, "500f402322677a4df10fb559aa63f22b")
                Log.e("MovieViewModel", "MOVIE ID: ${movie.id}")
                navController.navigate("movieDetail/${movie.id}")
            }
    ) {
        // Backdrop image
        RecommendationImage(movie.backdrop_path)
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomStart)
        ){
            Text(
                text = movie.title,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                modifier = Modifier
                    //    .padding(4.dp)
                    .align(Alignment.CenterHorizontally), // Center text horizontally
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row (
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()


            ){
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = Color.Yellow,
                    modifier = Modifier
                        .size(16.dp)
                )
                Text(
                    text = "${movie.vote_average}",
                    style = MaterialTheme.typography.labelMedium.copy(color = Color.White),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}