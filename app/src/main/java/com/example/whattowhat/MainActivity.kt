
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import com.example.whattowhat.model.Genre
import com.example.whattowhat.model.SortOptions
import com.example.whattowhat.model.SortOptions.sortOptions


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
                    text = provider.provider_name.toString(),
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
    val allProvidersOption = Provider(
        provider_id = 0,
        provider_name = "All Providers",
        display_priority = 0,
        logo_path = "",
        display_priorities = emptyMap()
    )
    val providersResponse = movieViewModel.getMovieProviders("500f402322677a4df10fb559aa63f22b").observeAsState(initial = emptyList())
    val providersState = listOf(allProvidersOption) + providersResponse.value
    val genres = GenreData.genres
    var expandedGenre by remember { mutableStateOf(false) }
    var expandedProvider by remember { mutableStateOf(false) }
    var selectedGenreId by remember { mutableStateOf(genres.first().id) }
    val selectedGenreName = genres.first { it.id == selectedGenreId }.name
    val selectedProviderState = remember { mutableStateOf(allProvidersOption) }
    val movies by movieViewModel.moviesState.observeAsState(initial = emptyList())
    var currentPage by remember { mutableStateOf(1) }
    val totalPages by movieViewModel.totalPages.observeAsState(1)
    val sorts = SortOptions.sortOptions
    var expandedSort by remember { mutableStateOf(false) }
    var selectedSortId by remember { mutableStateOf(sorts.first().id) }
    val selectedSortName = sorts.first { it.id == selectedSortId }.name
    var excludeAnimation by remember { mutableStateOf(false) }

    // Fetch movies when currentPage changes
    LaunchedEffect(currentPage, selectedGenreId, selectedProviderState.value, selectedSortId, excludeAnimation) {
        selectedProviderState.value?.let { provider ->
            val providerId = provider.provider_id?.toString()
            if (selectedGenreId == 0) {

            }
            val genreId = selectedGenreId?.toString()
            Log.e("MovieViewModel", "GENRE ID: ${genreId}")
            if(genreId == "0" && providerId != "0"){
                movieViewModel.getMoviesByProvider(
                    apiKey = "500f402322677a4df10fb559aa63f22b",
                    providerId = providerId ?: "",
                    page = currentPage,
                    sortBy = selectedSortId,
                    excludeAnimation = excludeAnimation
                )
            }else if(genreId != "0" && providerId == "0"){
                movieViewModel.getMoviesByGenre(
                    apiKey = "500f402322677a4df10fb559aa63f22b",
                    genreId = genreId.toString(),
                    page = currentPage,
                    sortBy = selectedSortId,
                    excludeAnimation = excludeAnimation)
            }else if(genreId == "0" && providerId == "0"){
                movieViewModel.getMovies(
                    apiKey = "500f402322677a4df10fb559aa63f22b",
                    page = currentPage,
                    sortBy = selectedSortId,
                    excludeAnimation = excludeAnimation)
            }else {
                movieViewModel.getMoviesByProviderAndGenre(
                    apiKey = "500f402322677a4df10fb559aa63f22b",
                    providerId = providerId ?: "",
                    genreId = genreId.toString(),
                    page = currentPage,
                    sortBy = selectedSortId,
                    excludeAnimation = excludeAnimation
                )
            }

        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        Row(
            modifier= Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            // Genre Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedGenre,
                onExpandedChange = { expandedGenre = !expandedGenre }
            ) {
                TextField(
                    value = selectedGenreName,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Genre") },
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
                    label = { Text("Watch Provider") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProvider) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedProvider,
                    onDismissRequest = { expandedProvider = false }
                ) {
                    providersState.forEach { provider ->
                        DropdownMenuItem(
                            text = { Text(text = provider.provider_name.toString()) },
                            onClick = {
                                selectedProviderState.value = provider
                                expandedProvider = false
                            }
                        )
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            // Sort Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedSort,
                onExpandedChange = { expandedSort = !expandedSort }
            ) {
                OutlinedTextField(
                    value = selectedSortName,
                    onValueChange = { },
                    label = { Text("Sort By") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSort) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedSort,
                    onDismissRequest = { expandedSort = false }
                ) {
                    sorts.forEach { sort ->
                        DropdownMenuItem(
                            text = { Text(text = sort.name) },
                            onClick = {
                                selectedSortId = sort.id
                                expandedSort = false
                            }
                        )
                    }
                }
            }

            Checkbox(
                checked = excludeAnimation,
                onCheckedChange = { excludeAnimation = !excludeAnimation }
            )
            Text(text = "Exclude Animation")

        }

        // Pagination controls styled with MaterialTheme
        PaginationControls(currentPage, totalPages) { newPage ->
            currentPage = newPage
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Movie grid
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 175.dp) // Set the number of columns to 3

        ) {
            items(movies.size) { index ->
                MovieItemView(movies[index])
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { onPageChange(currentPage - 1) },
            enabled = currentPage > 1,
            // Apply MaterialTheme styling
        ) {
            Text("Previous")
        }

        // Current Page TextField with outlined style
        OutlinedTextField(
            value = currentPage.toString(),
            onValueChange = { newValue -> onPageChange(newValue.toIntOrNull() ?: currentPage) },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .width(64.dp)
                .padding(horizontal = 8.dp),
            // More MaterialTheme styling
        )

        Button(
            onClick = { onPageChange(currentPage + 1) },
            enabled = currentPage < totalPages,
            // Apply MaterialTheme styling
        ) {
            Text("Next")
        }
    }
}

@Composable
fun MovieItemView(movie: MovieItem) {
    val imageUrlBase = "https://image.tmdb.org/t/p/w342"
    val logoUrlBase = "https://image.tmdb.org/t/p/w45"
    var genres = ""
    var providerLogos = ""
    movie.genre_ids.forEach { genreId ->
        val genre = GenreData.genres.first { it.id == genreId }
        genres += genre.name + ", "
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(180.dp)
    //    elevation = 4.dp // Use CardDefaults for elevation
    ) {
        Column {
            movie.poster_path?.let { posterPath ->
                val imageUrl = "$imageUrlBase$posterPath"
                Image(
                    painter = rememberImagePainter(imageUrl),
                    contentDescription = "Movie Poster",
                    modifier = Modifier
                        .height(270.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = movie.title + " (" + movie.release_date.substring(0,4) + ")" ,
                style = MaterialTheme.typography.bodySmall, // Change to a style that exists in Material3
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = "Rating: *${movie.vote_average}*",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
            Text(
                text = "${genres.substring(0, genres.length - 2)}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
fun MovieDetailsPage(movie: MovieItem) {
    Column {
        Text(text = movie.title)
        Text(text = "Rating: ${movie.vote_average}")
        Text(text = "Release date: ${movie.release_date}")
        // Add more details as needed
    }
}




