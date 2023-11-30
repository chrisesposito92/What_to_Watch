package com.example.whattowhat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.whattowhat.model.Genre
import com.example.whattowhat.model.Provider
import com.example.whattowhat.model.SortOption

@Composable
fun IncludeAnimationDialogDropdown(
    excludeAnimation: Boolean,
    onExcludeAnimationChanged: (Boolean) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    OutlinedButton(onClick = { showDialog = true }) {
        Text(text = if (excludeAnimation) "Exclude Animation" else "Include Animation")
    }

    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false }
        ) {
            Surface(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                LazyColumn {
                    items(2) { index ->
                        val excludeAnimation = index == 0
                        Text(
                            text = if (excludeAnimation) "Exclude Animation" else "Include Animation",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onExcludeAnimationChanged(excludeAnimation)
                                    showDialog = false
                                }
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MoviesOrTvDialogDropdown(
    isMoviesSelected: Boolean,
    onIsMoviesSelectedChanged: (Boolean) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    OutlinedButton(onClick = { showDialog = true }) {
        Text(text = if (isMoviesSelected) "Movies" else "TV")
    }

    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                LazyColumn {
                    items(2) { index ->
                        val isMoviesSelected = index == 0
                        Text(
                            text = if (isMoviesSelected) "Movies" else "TV",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onIsMoviesSelectedChanged(isMoviesSelected)
                                    showDialog = false
                                }
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun GenreDialogDropdown(
    genres: List<Genre>,
    selectedGenreId: Int,
    onGenreSelected: (Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val selectedGenreName = genres.firstOrNull { it.id == selectedGenreId }?.name ?: "All Genres"

    OutlinedButton(onClick = { showDialog = true }) {
        Text(text = selectedGenreName)
    }

    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false }
        ) {

            Surface(
                modifier = Modifier
//                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                LazyColumn {
                    items(genres.size) { index ->
                        val genre = genres[index]
                        Text(
                            text = genre.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onGenreSelected(genre.id)
                                    showDialog = false
                                }
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun YearDialogDropdown(
    years: List<String>,
    selectedYear: String,
    onYearSelected: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    OutlinedButton(onClick = { showDialog = true }) {
        Text(text = selectedYear)
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                LazyColumn {
                    items(years.size) { index ->
                        val year = years[index]
                        Text(
                            text = year,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onYearSelected(year)
                                    showDialog = false
                                }
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SortDialogDropdown(
    sortOptions: List<SortOption>,
    selectedSortId: String,
    onSortSelected: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val selectedSortName = sortOptions.first { it.id == selectedSortId }.name


    OutlinedButton(onClick = { showDialog = true }) {
        Text(text = selectedSortName)
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                LazyColumn {
                    items(sortOptions.size) { index ->
                        val sort = sortOptions[index]
                        Text(
                            text = sort.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSortSelected(sort.id)
                                    showDialog = false
                                }
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MinRatingDialogDropdown(
    ratings: List<Int>,
    selectedRating: Int,
    onRatingSelected: (Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    OutlinedButton(onClick = { showDialog = true }) {
        Text(text = selectedRating.toString())
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                LazyColumn {
                    items(ratings.size) { index ->
                        val rating = ratings[index]
                        Text(
                            text = rating.toString(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onRatingSelected(rating)
                                    showDialog = false
                                }
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProviderDialogDropdown(
    providers: List<Provider>,
    selectedProvider: Int,
    onProviderSelected: (Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val selectedProviderName = providers.firstOrNull { it.provider_id == selectedProvider }?.provider_name ?: "All Providers"


    OutlinedButton(onClick = { showDialog = true }) {
        Text(text = selectedProviderName)
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                LazyColumn {
                    items(providers.size) { index ->
                        val provider = providers[index]
                        Text(
                            text = provider.provider_name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onProviderSelected(provider.provider_id)
                                    showDialog = false
                                }
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}


