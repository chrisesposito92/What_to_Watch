package com.example.whattowhat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchMoviesScreen(movieViewModel: MovieViewModel = viewModel(), navController: NavController){
    val movies by movieViewModel.moviesSearchState.observeAsState(initial = emptyList())

    val textState = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 15.dp, top = 0.dp, end = 0.dp, bottom = 0.dp)
    ) {
        Row {
            TextField(
                value = textState.value,
                onValueChange = { newValue -> textState.value = newValue },
                label = { Text("Search") },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            Button(
                onClick = {
                    movieViewModel.searchMovies("500f402322677a4df10fb559aa63f22b", textState.value)
                }
            ) {
                Text("Search")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            state = rememberLazyGridState(),
            columns = GridCells.Fixed(8)
        ) {
            items(movies.size) { index ->
                val movie = movies[index]
                MovieItemView(movie, viewModel(), navController)
            }
        }
    }
}