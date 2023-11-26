
package com.example.whattowhat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.whattowhat.model.Genre
import com.example.whattowhat.model.MovieGenreData
import com.example.whattowhat.model.MovieItem
import com.example.whattowhat.model.Provider
import com.example.whattowhat.model.SortOption
import com.example.whattowhat.model.SortOptions
import com.example.whattowhat.model.Years
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.example.whattowhat.model.TvGenreData
import kotlinx.coroutines.launch
import com.example.whattowhat.model.TvItem
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "providerSelection") {
                composable("providerSelection") { ProviderSelectionScreen(viewModel(), navController) }
                composable("movietvList/{selectedProviders}", arguments = listOf(navArgument("selectedProviders") { type = NavType.StringType })) { backStackEntry ->
                    val selectedProviders = backStackEntry.arguments?.getString("selectedProviders")?.split(",")?.mapNotNull { it.toInt() }
                    MovieTvListScreen(viewModel(), navController, selectedProviders)
                }
       //         composable("home") { MovieTvListScreen(viewModel(), navController) }
                composable("movieDetail/{movieId}", arguments = listOf(navArgument("movieId") { type = NavType.StringType })) { backStackEntry ->
                    val movieId = backStackEntry.arguments?.getString("movieId")
                    MovieDetailsPage(movieId, viewModel(), navController)
                }
                composable("tvDetail/{tvId}", arguments = listOf(navArgument("tvId") { type = NavType.StringType })) { backStackEntry ->
                    val tvId = backStackEntry.arguments?.getString("tvId")
                    TvDetailsPage(tvId, viewModel(), navController)
                }
                // Add other composables for different routes as needed
            }
        }

    }
}

@Composable
fun ProviderSelectionScreen(movieViewModel: MovieViewModel, navController: NavController) {

    val providersResponse = movieViewModel.getMovieProviders("500f402322677a4df10fb559aa63f22b").observeAsState(initial = emptyList())
    val providersState = providersResponse.value
    val sortedProviders = providersState.sortedBy { it.display_priority }
    val selectedProviders = remember { mutableStateListOf<Provider>() }
    var selectAll by remember { mutableStateOf(false) }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text("Provider Selection", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Button(onClick = {
                selectAll = !selectAll
                if (selectAll) {
                    selectedProviders.clear()
                    selectedProviders.addAll(sortedProviders)
                } else {
                    selectedProviders.clear()
                }
            }) {
                Text(if (selectAll) "Deselect All" else "Select All")
            }

            Button(onClick = {
                navController.navigate("movietvList/${selectedProviders.joinToString(",") { it.provider_id.toString() }}")
            }) {
                Text("Continue")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(sortedProviders.size) { index ->
                val provider = sortedProviders[index]
                ProviderListItem(provider, selectedProviders)
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun ProviderListItem(provider: Provider, selectedProviders: SnapshotStateList<Provider>) {
    val isSelected = derivedStateOf { provider in selectedProviders }
    Row(Modifier.clickable {
        if (isSelected.value) {
            selectedProviders.remove(provider)
        } else {
            selectedProviders.add(provider)
        }
    }) {
        Switch(checked = isSelected.value, onCheckedChange = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(modifier = Modifier.padding(top = 6.dp), text = provider.provider_name)
    }
}


@SuppressLint("SuspiciousIndentation")
@Composable
fun MovieTvListScreen(movieViewModel: MovieViewModel = viewModel(), navController: NavController, selectedProviders: List<Int>?) {
    val allProvidersOption = Provider(
        provider_id = 0,
        provider_name = "All Providers",
        display_priority = 0,
        logo_path = "",
        display_priorities = emptyMap()
    )
    Log.e("MovieViewModel", "SELECTED PROVIDERS: ${selectedProviders}")
    val providersResponse = movieViewModel.getMovieProviders("500f402322677a4df10fb559aa63f22b").observeAsState(initial = emptyList())
    var providersState = listOf(allProvidersOption) + providersResponse.value
    providersState = if (selectedProviders != null) {
        providersState.filter { it.provider_id in selectedProviders }
    } else {
        providersState.filter { it.provider_id != 0 }
    }

    providersState = listOf(allProvidersOption) + providersState
    var isMoviesSelected by remember { mutableStateOf(true) }
    var genres by remember { mutableStateOf(MovieGenreData.genres) }
    val years = Years.years
    val ratings = (1 .. 10).toList()
    var selectedRating by remember { mutableStateOf(ratings.first()) }
    var selectedYear by remember { mutableStateOf(years.first()) }
    var selectedGenreId by remember { mutableStateOf(genres.first().id) }
    var selectedProviderState = remember { mutableStateOf(allProvidersOption) }
    val movies by movieViewModel.moviesState.observeAsState(initial = emptyList())
    val tv by movieViewModel.tvState.observeAsState(initial = emptyList())
    var currentPage by remember { mutableStateOf(1) }
    val totalPagesMovie by movieViewModel.totalPagesMovie.observeAsState(1)
    val totalPagesTv by movieViewModel.totalPagesTv.observeAsState(1)
    var totalPages by remember { mutableStateOf(1)}
    val sorts = SortOptions.sortOptions
    var selectedSortId by remember { mutableStateOf(sorts.first().id) }
    var excludeAnimation by remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState() // LazyGridState for LazyVerticalGrid
    var filtersVisible by remember { mutableStateOf(true) }

    LaunchedEffect(isMoviesSelected) {
        genres = if (isMoviesSelected) {
            MovieGenreData.genres
        } else {
            TvGenreData.genres
        }

        // Reset the selected genre when switching between Movies and TV
        selectedGenreId = genres.first().id
    }


    // Fetch movies when currentPage changes
    LaunchedEffect(
        currentPage,
        selectedGenreId,
        selectedProviderState.value,
        selectedSortId,
        excludeAnimation,
        selectedYear,
        selectedRating,
        isMoviesSelected
    ) {
        selectedProviderState.value?.let { provider ->
            val providerId = provider.provider_id?.toString()
            val genreId = selectedGenreId?.toString()

            Log.e("MovieViewModel", "GENRE ID: ${genreId}")
            if(isMoviesSelected){
                if (selectedProviders != null) {
                    val providerId2 = selectedProviders.joinToString(", ")
                    Log.e("MovieViewModel", "PROVIDER ID: ${providerId2}")
                    movieViewModel.getMovies(
                        apiKey = "500f402322677a4df10fb559aa63f22b",
                        genreId = if(selectedGenreId == 0) null else selectedGenreId.toString(),
                        providerId = if(provider.provider_id == 0) selectedProviders.joinToString("|") else providerId,
                        year = if (selectedYear == "All Years") null else selectedYear.toInt(),
                        voteAverage = if (selectedRating == 0) null else selectedRating,
                        page = currentPage,
                        sortBy = selectedSortId,
                        excludeAnimation = excludeAnimation
                    )
                }
            }else{
                movieViewModel.getTV(
                    apiKey = "500f402322677a4df10fb559aa63f22b",
                    genreId = if(selectedGenreId == 0) null else selectedGenreId.toString(),
                    providerId = if(selectedProviderState.value == allProvidersOption) null else selectedProviderState.value.provider_id.toString(),
                    year = if (selectedYear == "All Years") null else selectedYear.toInt(),
                    voteAverage = if (selectedRating == 0) null else selectedRating,
                    page = currentPage,
                    sortBy = selectedSortId,
                    excludeAnimation = excludeAnimation
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
    ){
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ){
            FilterRow(
                genres = genres,
                selectedGenreId = selectedGenreId,
                onGenreSelected = { selectedGenreId = it },
                years = years,
                selectedYear = selectedYear,
                onYearSelected = { selectedYear = it },
                providers = providersState,
                selectedProvider = selectedProviderState.value,
                onProviderSelected = { selectedProviderState.value = it },
                filtersVisible = filtersVisible,
                ratings = ratings,
                selectedRating = selectedRating,
                onRatingSelected = { selectedRating = it },
                sortOptions = sorts,
                selectedSortId = selectedSortId,
                onSortSelected = { selectedSortId = it },
                excludeAnimation = excludeAnimation,
                onExcludeAnimationChanged = { excludeAnimation = it },
                isMoviesSelected = isMoviesSelected,
                onIsMoviesSelectedChanged = { isMoviesSelected = it }
            )
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ){
            Button(
                onClick = { filtersVisible = !filtersVisible },
                modifier = Modifier.padding(start = 20.dp)
            ) {
                Text(if (filtersVisible) "Hide Filters" else "Show Filters")
            }

        }

        if (isMoviesSelected) {
            totalPages = totalPagesMovie
            Column (
                modifier = Modifier
                    .fillMaxSize()
            ){
                Box (
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ){
                    LazyVerticalGrid(
                        state = gridState,
                        //    columns = GridCells.Adaptive(minSize = 154.dp)
                        columns = GridCells.Fixed(7)
                    ) {
                        items(movies.size) { index ->
                            MovieItemView(movies[index], viewModel(), navController)
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    PaginationControls(currentPage, totalPages, gridState) { newPage ->
                        currentPage = newPage
                    }
                }

            }

        } else {
            totalPages = totalPagesTv
            Column (
                modifier = Modifier
                    .fillMaxSize()
            ){
                Box (
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ){
                    LazyVerticalGrid(
                        state = gridState,
                        //    columns = GridCells.Adaptive(minSize = 154.dp)
                        columns = GridCells.Fixed(7)
                    ) {
                        items(tv.size) { index ->
                            TvItemView(tv[index], viewModel(), navController)
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    PaginationControls(currentPage, totalPages, gridState) { newPage ->
                        currentPage = newPage
                    }
                }

            }
        }
    }
}

@Composable
fun FilterRow(
    genres: List<Genre>,
    selectedGenreId: Int,
    onGenreSelected: (Int) -> Unit,
    years: List<String>,
    selectedYear: String,
    onYearSelected: (String) -> Unit,
    providers: List<Provider>,
    selectedProvider: Provider,
    onProviderSelected: (Provider) -> Unit,
    filtersVisible: Boolean,
    ratings: List<Int>,
    selectedRating: Int,
    onRatingSelected: (Int) -> Unit,
    sortOptions: List<SortOption>,
    selectedSortId: String,
    onSortSelected: (String) -> Unit,
    excludeAnimation: Boolean,
    onExcludeAnimationChanged: (Boolean) -> Unit,
    isMoviesSelected: Boolean,
    onIsMoviesSelectedChanged: (Boolean) -> Unit
) {
    if(filtersVisible){
        SortDialogDropdown(
            sortOptions = sortOptions,
            selectedSortId = selectedSortId,
            onSortSelected = onSortSelected
        )
        GenreDialogDropdown(
            genres = genres,
            selectedGenreId = selectedGenreId,
            onGenreSelected = onGenreSelected
        )
        YearDialogDropdown(
            years = years,
            selectedYear = selectedYear,
            onYearSelected = onYearSelected
        )
        MinRatingDialogDropdown(
            ratings = ratings,
            selectedRating = selectedRating,
            onRatingSelected = onRatingSelected
        )
        ProviderDialogDropdown(
            providers = providers,
            selectedProvider = selectedProvider,
            onProviderSelected = onProviderSelected
        )
        IncludeAnimationDialogDropdown(
            excludeAnimation = excludeAnimation,
            onExcludeAnimationChanged = onExcludeAnimationChanged
        )
        MoviesOrTvDialogDropdown(
            isMoviesSelected = isMoviesSelected,
            onIsMoviesSelectedChanged = onIsMoviesSelectedChanged
        )
    }
}

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
    selectedProvider: Provider,
    onProviderSelected: (Provider) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val selectedProviderName = selectedProvider.provider_name


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
                                    onProviderSelected(provider)
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



@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    gridState: LazyGridState,
    onPageChange: (Int) -> Unit
) {
    val currentPageString = currentPage.toString() + " of " + totalPages.toString()
    val textFieldWidth = (currentPageString.length * 10 + 45).dp
    LaunchedEffect(currentPage) {
        gridState.scrollToItem(index = 0)
    }
        Button(
            onClick = { onPageChange(1) },
            enabled = currentPage > 2,
            // Apply MaterialTheme styling
        ) {
            Text("First")
        }
        Button(
            onClick = { onPageChange(currentPage - 1) },
            enabled = currentPage > 1,
            // Apply MaterialTheme styling
        ) {
            Text("Previous")
        }

        // Current Page TextField with outlined style
        Text(
            text = currentPageString,
            style = TextStyle(
                fontSize = 24.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(textFieldWidth)
                .padding(8.dp)
                .border(1.dp, Color.Black, shape = RoundedCornerShape(50))
            // More MaterialTheme styling
        )

        Button(
            onClick = {
                onPageChange(currentPage + 1)
            },
            enabled = currentPage < totalPages
        ) {
            Text("Next")
        }

        Button(
            onClick = { onPageChange(totalPages) },
            enabled = currentPage < totalPages - 1,
            // Apply MaterialTheme styling
        ) {
            Text("Last")
        }

}

@Composable
fun MovieItemView(movie: MovieItem, movieViewModel: MovieViewModel, navController: NavController) {
    val context = LocalContext.current
    val imageUrlBase = "https://image.tmdb.org/t/p/w780"
    var genres = movie.genre_ids.joinToString(", ") { genreId ->
        MovieGenreData.genres.first { it.id == genreId }.name
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
                //    movieViewModel.fetchTrailerMovie(movie.id, "500f402322677a4df10fb559aa63f22b")
                Log.e("MovieViewModel", "MOVIE ID: ${movie.id}")
                navController.navigate("movieDetail/${movie.id}")
            },
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
            Text(
                text = "Rating: ${movie.vote_average}",
                style = TextStyle(
                    fontSize = 10.sp,
                    color = Color.Black
                ),
                modifier = Modifier.padding(start = 10.dp, top = 2.dp, end = 10.dp, bottom = 0.dp)
            )
            Text(
                text = "${genres.substring(0, genres.length)}",
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
        if (!videoId.isNullOrEmpty()) {
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
fun TvItemView(tv: TvItem, movieViewModel: MovieViewModel, navController: NavController ) {
    val context = LocalContext.current
    val imageUrlBase = "https://image.tmdb.org/t/p/w185"
    var genres = tv.genre_ids.joinToString(", ") { genreId ->
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
                text = "${genres.substring(0, genres.length)}",
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
        if (!videoId.isNullOrEmpty()) {
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
fun MovieDetailsPage(movieId: String?, movieViewModel: MovieViewModel, navController: NavController) {
    // Assuming movieDetails is a LiveData object that holds the details
    val movieDetails by movieViewModel.movieDetails.observeAsState()
    val movieProviders by movieViewModel.movieProviders.observeAsState()
    val movieRecommendations by movieViewModel.movieRecommendations.observeAsState()
    val context = LocalContext.current
    val videoMovieId by movieViewModel.videoMovieId.observeAsState()

    LaunchedEffect(movieId) {
        movieViewModel.fetchMovieDetails(movieId!!.toInt(), "500f402322677a4df10fb559aa63f22b")
        movieViewModel.fetchMovieWatchProviders(movieId!!.toInt(), "500f402322677a4df10fb559aa63f22b")
        movieViewModel.getMovieRecommendations(movieId!!.toInt(), "500f402322677a4df10fb559aa63f22b")

    }

    Log.e("MovieViewModel", "MOVIE ID: ${movieId}")
    Log.e("MovieViewModel", "MOVIE DETAILS: ${movieDetails}")
    Log.e("MovieViewModel", "MOVIE PROVIDERS: ${movieProviders}")
    Log.e("MovieViewModel", "MOVIE RECOMMENDATIONS: ${movieRecommendations}")

    movieDetails?.let { movie ->
        Box {
            // Backdrop image
            BackdropImage(movie.backdrop_path)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomStart)
            ) {
                // Title
                Text(
                    text = "${movie.title} (${movie.release_date.take(4)})",
                    style = MaterialTheme.typography.displayMedium.copy(color = Color.White),
                    modifier = Modifier.shadow(2.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Rating
                Row {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color.Yellow
                    )
                    Text(
                        text = "${movie.vote_average} / 10",
                        style = MaterialTheme.typography.headlineSmall.copy(color = Color.White),
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
                    text = "${movie.genres.joinToString(", ") { it.name }}",
                    style = MaterialTheme.typography.labelLarge.copy(color = Color.White),
                    modifier = Modifier.shadow(2.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Providers
                movieProviders?.let { providers ->
                    LazyRow {
                        items(providers.size) { index ->
                            providers[index].let { provider ->
                                ProviderLogo(provider)
                            }
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
                Button(
                    onClick = {
                        movieViewModel.fetchTrailerMovie(movie.id, "500f402322677a4df10fb559aa63f22b")
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Play Trailer")
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Movie Recommendations Section

                var movieRecommendationsFiltered = movieRecommendations?.filter { it.vote_count >= 250 }?.distinctBy { it.id }
                movieRecommendationsFiltered?.let { recommendations ->
                    Text(
                        text = "Similar Movies",
                        style = MaterialTheme.typography.headlineMedium.copy(color = Color.White),
                        modifier = Modifier.padding(top = 0.dp, bottom = 8.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
               //         contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(recommendations.size) { index ->
                            var movie = recommendations[index]
                        //    var nextMovie = recommendations.getOrNull(index + 1)
                        //    if(movie.vote_count >= 250 && movie != nextMovie){
                                RecommendationCard(movie, movieViewModel, navController)
                        //    }
                        }
                    }
                }

            }
        }
    } ?: Text(text = "Loading movie details...")

    // Observe the video ID LiveData.
    val videoIdEvent by movieViewModel.videoMovieId.observeAsState()
    videoIdEvent?.getContentIfNotHandled()?.let { videoId ->
        if (!videoId.isNullOrEmpty()) {
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
fun RecommendationCard(movie: MovieItem, movieViewModel: MovieViewModel = viewModel(), navController: NavController) {
    Box(
        modifier = Modifier
            .width(150.dp)
            .height(250.dp)
            .padding(4.dp)
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
            Text(
                text = "Rating: ${movie.vote_average}",
                style = MaterialTheme.typography.labelMedium.copy(color = Color.White),
                modifier = Modifier
                //    .padding(4.dp)
                    .align(Alignment.CenterHorizontally), // Center text horizontally
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
     //   }
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
            contentScale = ContentScale.Crop,
            alpha = 0.9f
        )
        // Dark gradient overlay if needed
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()

        )
    }
}

@Composable
fun BackdropImage(backdropPath: String?) {
    val imageUrlBase = "https://image.tmdb.org/t/p/original"
    backdropPath?.let {
        val imageUrl = "$imageUrlBase$it"
        Log.e("MovieViewModel", "IMAGE URL: ${imageUrl}")
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = "Backdrop Image",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(), // Adjust height accordingly
            contentScale = ContentScale.Crop,
            alpha = 0.5f
        )
        // Dark gradient overlay if needed
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black),
                        startY = 300f // Adjust gradient to your liking
                    )
                )
        )
    }
}

@Composable
fun TvDetailsPage(tvId: String?, movieViewModel: MovieViewModel, navController: NavController) {
    Column {
        Text(text = tvId.toString() )
        // Add more details as needed
    }
}