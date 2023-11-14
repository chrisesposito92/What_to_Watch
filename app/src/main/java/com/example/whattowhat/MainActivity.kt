
package com.example.whattowhat

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import com.example.whattowhat.model.Provider
import com.example.whattowhat.model.GenreData
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.navigation.navArgument
import com.example.whattowhat.model.MovieItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberImagePainter
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.TextField
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieApp()
        }
    }
}

@Composable
fun MovieApp(movieViewModel: MovieViewModel = viewModel()) {
    ProviderDetailScreen()
}

@Composable
fun ProviderList(providers: List<Provider>, onProviderSelected: (Provider) -> Unit) {
    var selectedProvider by remember { mutableStateOf<Provider?>(null) }

    Column {
        providers.forEach { provider ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        selectedProvider = provider
                        onProviderSelected(provider)
                    }
            ) {
                RadioButton(
                    selected = (selectedProvider == provider),
                    onClick = {
                        selectedProvider = provider
                        onProviderSelected(provider)
                    }
                )
                Text(
                    text = provider.provider_name,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderDetailScreen(movieViewModel: MovieViewModel = viewModel()) {
    val context = LocalContext.current
    val providersState = movieViewModel.getMovieProviders("500f402322677a4df10fb559aa63f22b").observeAsState(initial = emptyList())
    val genres = GenreData.genres
    var expandedGenre by remember { mutableStateOf(false) }
    var expandedProvider by remember { mutableStateOf(false) }
    var selectedGenreId by remember { mutableStateOf(genres.first().id) }
    val selectedGenreName = genres.first { it.id == selectedGenreId }.name
    val selectedProviderState = remember { mutableStateOf<Provider?>(null) }
    val movies by movieViewModel.moviesState.observeAsState(initial = emptyList())

    LaunchedEffect(providersState.value) {
        if (providersState.value.isNotEmpty()) {
            selectedProviderState.value = providersState.value.first()

        }
    }

    // Triggered when the selected genre or provider changes
    LaunchedEffect(selectedGenreId, selectedProviderState.value) {
        selectedProviderState.value?.let { provider ->
            // You need to map the selected genre to its ID
            movieViewModel.getMoviesByProviderAndGenre("500f402322677a4df10fb559aa63f22b", provider.provider_id.toString(),
                selectedGenreId.toString()
            )
        }
    }

    // This is the UI that will be displayed on the screen
    Column {
        // Genre Dropdown
        ExposedDropdownMenuBox(
            expanded = expandedGenre,
            onExpandedChange = { expandedGenre = !expandedGenre }
        ) {
            TextField(
                value = selectedGenreName,
                onValueChange = {  },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGenre) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedGenre,
                onDismissRequest = { expandedGenre = false }
            ) {
                genres.forEach { genre ->
                    DropdownMenuItem(
                        text = { Text(text = genre.name) },
                        onClick = {
                            selectedGenreId = genre.id
                            expandedGenre = false
                        }
                    )
                }
            }
        }

        // Provider Dropdown
        ExposedDropdownMenuBox(
            expanded = expandedProvider,
            onExpandedChange = { expandedProvider = !expandedProvider }
        ) {
            TextField(
                value = selectedProviderState.value?.provider_name ?: "",
                onValueChange = { /* Ignored as the field is read-only */ },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProvider) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedProvider,
                onDismissRequest = { expandedProvider = false }
            ) {
                providersState.value.forEach { provider ->
                    DropdownMenuItem(
                        text = { Text(text = provider.provider_name) },
                        onClick = {
                            selectedProviderState.value = provider
                            expandedProvider = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Movie list
        LazyColumn {
            items(movies) { movie ->
                MovieItemView(movie)
            }
        }
    }
}

@Composable
fun MovieItemView(movie: MovieItem) {
    val imageUrlBase = "https://image.tmdb.org/t/p/w500"

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    //    elevation = 4.dp // Use CardDefaults for elevation
    ) {
        Column {
            movie.poster_path?.let { posterPath ->
                val imageUrl = "$imageUrlBase$posterPath"
                Image(
                    painter = rememberImagePainter(imageUrl),
                    contentDescription = "Movie Poster",
                    modifier = Modifier
                        .height(400.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = movie.title,
                style = MaterialTheme.typography.bodyLarge, // Change to a style that exists in Material3
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}


