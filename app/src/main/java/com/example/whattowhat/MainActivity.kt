
package com.example.whattowhat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
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
import coil.compose.rememberImagePainter
import com.example.whattowhat.model.Genre
import com.example.whattowhat.model.GenreData
import com.example.whattowhat.model.MovieItem
import com.example.whattowhat.model.Provider
import com.example.whattowhat.model.SortOption
import com.example.whattowhat.model.SortOptions
import com.example.whattowhat.model.Years
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.TextField
import androidx.core.util.rangeTo
import com.example.whattowhat.model.NumberOfVotesOptions
import kotlinx.coroutines.launch
import com.example.whattowhat.model.TvItem


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val movieViewModel: MovieViewModel = viewModel()
            MovieApp(movieViewModel)
        }
    }
}

@Composable
fun MovieApp(movieViewModel: MovieViewModel) {
    ProviderDetailScreen(movieViewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderDetailScreen(movieViewModel: MovieViewModel) {
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
    val years = Years.years
    val votes = NumberOfVotesOptions.numOfVotes
    val ratings = (1 .. 10).toList()
    var selectedRating by remember { mutableStateOf(ratings.first()) }
    var selectedYear by remember { mutableStateOf(years.first()) }
    var selectedVote by remember { mutableStateOf(250) }
    var selectedGenreId by remember { mutableStateOf(genres.first().id) }
    val selectedProviderState = remember { mutableStateOf(allProvidersOption) }
    val movies by movieViewModel.moviesState.observeAsState(initial = emptyList())
    val tv by movieViewModel.tvState.observeAsState(initial = emptyList())
    var currentPage by remember { mutableStateOf(1) }
    val totalPages by movieViewModel.totalPages.observeAsState(1)
    val sorts = SortOptions.sortOptions
    var selectedSortId by remember { mutableStateOf(sorts.first().id) }
    var excludeAnimation by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope() // Coroutine scope for launching suspend functions
    val gridState = rememberLazyGridState() // LazyGridState for LazyVerticalGrid
    val numberOfColumns = 6 // Adjust this based on your grid setup
    val heightOfGridItem = 750.dp // Adjust this based on the size of your grid items
    var paddingBottom by remember { mutableStateOf(0.dp) }
    var filtersVisible by remember { mutableStateOf(true) }
    var isMoviesSelected by remember { mutableStateOf(true) }

    // Fetch movies when currentPage changes
    LaunchedEffect(
        currentPage,
        selectedGenreId,
        selectedProviderState.value,
        selectedSortId,
        excludeAnimation,
        selectedYear,
        selectedVote,
        selectedRating
    ) {
        selectedProviderState.value?.let { provider ->
            val providerId = provider.provider_id?.toString()
            val genreId = selectedGenreId?.toString()

            Log.e("MovieViewModel", "GENRE ID: ${genreId}")

            movieViewModel.getMovies(
                apiKey = "500f402322677a4df10fb559aa63f22b",
                genreId = if(selectedGenreId == 0) null else selectedGenreId.toString(),
                providerId = if(selectedProviderState.value == allProvidersOption) null else selectedProviderState.value.provider_id.toString(),
                year = if (selectedYear == "All Years") null else selectedYear.toInt(),
                voteCount = if (selectedVote == 0) null else selectedVote,
                voteAverage = if (selectedRating == 0) null else selectedRating,
                page = currentPage,
                sortBy = selectedSortId,
                excludeAnimation = excludeAnimation
            )

            movieViewModel.getTV(
                apiKey = "500f402322677a4df10fb559aa63f22b",
                genreId = if(selectedGenreId == 0) null else selectedGenreId.toString(),
                providerId = if(selectedProviderState.value == allProvidersOption) null else selectedProviderState.value.provider_id.toString(),
                year = if (selectedYear == "All Years") null else selectedYear.toInt(),
                voteCount = if (selectedVote == 0) null else selectedVote,
                voteAverage = if (selectedRating == 0) null else selectedRating,
                page = currentPage,
                sortBy = selectedSortId,
                excludeAnimation = excludeAnimation
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        FilterRowOne(
            genres = genres,
            selectedGenreId = selectedGenreId,
            onGenreSelected = { selectedGenreId = it },
            years = years,
            selectedYear = selectedYear,
            onYearSelected = { selectedYear = it },
            votes = votes,
            selectedVote = selectedVote,
            onVoteSelected = { selectedVote = it },
            providers = providersState,
            selectedProvider = selectedProviderState.value,
            onProviderSelected = { selectedProviderState.value = it },
            filtersVisible = filtersVisible
        )
        FilterRowTwo(
            sortOptions = sorts,
            selectedSortId = selectedSortId,
            onSortSelected = { selectedSortId = it },
            excludeAnimation = excludeAnimation,
            onExcludeAnimationChanged = { excludeAnimation = it },
            filtersVisible = filtersVisible,
            ratings = ratings,
            selectedRating = selectedRating,
            onRatingSelected = { selectedRating = it }
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ){
            Button(
                onClick = { filtersVisible = !filtersVisible },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(if (filtersVisible) "Hide Filters" else "Show Filters")
            }
            // Add this Switch to toggle between Movies and TV
            Switch(
                checked = isMoviesSelected,
                onCheckedChange = { isMoviesSelected = it },
                modifier = Modifier.padding(start = 8.dp)
            )
            Text(text = if (isMoviesSelected) "Movies" else "TV")
        }

        Row(modifier = Modifier.fillMaxWidth()){
            // Pagination controls styled with MaterialTheme
            PaginationControls(currentPage, totalPages) { newPage ->
                currentPage = newPage
            }

        }


        Spacer(modifier = Modifier.height(10.dp))

        if (isMoviesSelected) {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Adaptive(minSize = 154.dp)
            ) {
                items(movies.size) { index ->
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused && index >= movies.size - numberOfColumns) {
                                    paddingBottom = heightOfGridItem
                                    coroutineScope.launch {
                                        gridState.animateScrollToItem(index)
                                    }
                                } else {
                                    paddingBottom = 0.dp
                                }
                            }
                    ) {
                        MovieItemView(movies[index], viewModel())
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Adaptive(minSize = 154.dp)
            ) {
                items(tv.size) { index ->
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused && index >= tv.size - numberOfColumns) {
                                    paddingBottom = heightOfGridItem
                                    coroutineScope.launch {
                                        gridState.animateScrollToItem(index)
                                    }
                                } else {
                                    paddingBottom = 0.dp
                                }
                            }
                    ) {
                        TvItemView(tv[index], viewModel()) // Replace with your function to display a TvItem
                    }
                }
            }
        }
    }
}

@Composable
fun FilterRowOne(
    genres: List<Genre>,
    selectedGenreId: Int,
    onGenreSelected: (Int) -> Unit,
    years: List<String>,
    selectedYear: String,
    onYearSelected: (String) -> Unit,
    votes: List<Int>,
    selectedVote: Int,
    onVoteSelected: (Int) -> Unit,
    providers: List<Provider>,
    selectedProvider: Provider,
    onProviderSelected: (Provider) -> Unit,
    filtersVisible: Boolean
) {
    if(filtersVisible){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GenreDropdown(
                genres = genres,
                selectedGenreId = selectedGenreId,
                onGenreSelected = onGenreSelected
            )
            YearDropdown(
                years = years,
                selectedYear = selectedYear,
                onYearSelected = onYearSelected
            )
            ProviderDropdown(
                providers = providers,
                selectedProvider = selectedProvider,
                onProviderSelected = onProviderSelected
            )
            MinVoteCountDropdown(
                votes = votes,
                selectedVote = selectedVote,
                onVoteSelected = onVoteSelected
            )
        }
    }
}

@Composable
fun FilterRowTwo(
    sortOptions: List<SortOption>,
    selectedSortId: String,
    onSortSelected: (String) -> Unit,
    excludeAnimation: Boolean,
    onExcludeAnimationChanged: (Boolean) -> Unit,
    filtersVisible: Boolean,
    ratings: List<Int>,
    selectedRating: Int,
    onRatingSelected: (Int) -> Unit
) {
    if(filtersVisible){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SortDropdown(
                sortOptions = sortOptions,
                selectedSortId = selectedSortId,
                onSortSelected = onSortSelected
            )
            MinRatingDropdown(
                ratings = ratings,
                selectedRating = selectedRating,
                onRatingSelected = onRatingSelected
            )
            Checkbox(
                checked = excludeAnimation,
                onCheckedChange = onExcludeAnimationChanged,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = "Exclude Animation")

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreDropdown(
    genres: List<Genre>,
    selectedGenreId: Int,
    onGenreSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedGenreName = genres.first { it.id == selectedGenreId }.name

    Box (
        modifier = Modifier
            .clickable { expanded = !expanded }
            .width(200.dp)
    ){
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .width(200.dp)
        ) {
            TextField(
                value = selectedGenreName,
                onValueChange = { },
                readOnly = true,
                label = { Text("Genre") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }

            ) {
                genres.forEach { genre ->
                    DropdownMenuItem(
                        text = { Text(text = genre.name) },
                        onClick = {
                            onGenreSelected(genre.id)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearDropdown(
    years: List<String>,
    selectedYear: String,
    onYearSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box (
        modifier = Modifier
            .clickable { expanded = !expanded }
            .width(200.dp)
    ){
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .width(200.dp)
        ) {
            TextField(
                value = selectedYear,
                onValueChange = { },
                readOnly = true,
                label = { Text("Year") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                years.forEach { year ->
                    DropdownMenuItem(
                        text = { Text(text = year) },
                        onClick = {
                            onYearSelected(year)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinVoteCountDropdown(
    votes: List<Int>,
    selectedVote: Int,
    onVoteSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box (
        modifier = Modifier
            .clickable { expanded = !expanded }
            .width(200.dp)
    ){
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .width(200.dp)
        ) {
            TextField(
                value = selectedVote.toString(),
                onValueChange = { },
                readOnly = true,
                label = { Text("Min Number of Votes") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                votes.forEach { vote ->
                    DropdownMenuItem(
                        text = { Text(text = vote.toString()) },
                        onClick = {
                            onVoteSelected(vote)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinRatingDropdown(
    ratings: List<Int>,
    selectedRating: Int,
    onRatingSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box (
        modifier = Modifier
            .clickable { expanded = !expanded }
            .width(200.dp)
    ){
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .width(200.dp)
        ) {
            TextField(
                value = selectedRating.toString(),
                onValueChange = { },
                readOnly = true,
                label = { Text("Min Rating") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                ratings.forEach { rating ->
                    DropdownMenuItem(
                        text = { Text(text = rating.toString()) },
                        onClick = {
                            onRatingSelected(rating)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderDropdown(
    providers: List<Provider>,
    selectedProvider: Provider,
    onProviderSelected: (Provider) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedProviderName = selectedProvider.provider_name

    Box (
        modifier = Modifier
            .clickable { expanded = !expanded }
            .width(200.dp)
    ){
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .width(200.dp)
        ) {
            TextField(
                value = selectedProviderName,
                onValueChange = { /* Ignored as the field is read-only */ },
                readOnly = true,
                label = { Text("Watch Provider") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                providers.forEach { provider ->
                    DropdownMenuItem(
                        text = { Text(text = provider.provider_name) },
                        onClick = {
                            onProviderSelected(provider)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortDropdown(
    sortOptions: List<SortOption>,
    selectedSortId: String,
    onSortSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedSortName = sortOptions.first { it.id == selectedSortId }.name

    Box (
        modifier = Modifier
            .clickable { expanded = !expanded }
            .width(200.dp)
    ){
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .width(200.dp)
        ) {
            TextField(
                value = selectedSortName,
                onValueChange = { },
                label = { Text("Sort By") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                sortOptions.forEach { sortOption ->
                    DropdownMenuItem(
                        text = { Text(text = sortOption.name) },
                        onClick = {
                            onSortSelected(sortOption.id)
                            expanded = false
                        }
                    )
                }
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
    val currentPageString = currentPage.toString()
    val textFieldWidth = (currentPageString.length * 10).dp
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
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
        OutlinedTextField(
            value = currentPage.toString(),
            onValueChange = { newValue -> onPageChange(newValue.toIntOrNull() ?: currentPage) },
            singleLine = true,
            readOnly = true,
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

        Button(
            onClick = { onPageChange(totalPages) },
            enabled = currentPage < totalPages - 1,
            // Apply MaterialTheme styling
        ) {
            Text("Last")
        }
    }
}

@Composable
fun MovieItemView(movie: MovieItem, viewModel: MovieViewModel) {
    val context = LocalContext.current
    val imageUrlBase = "https://image.tmdb.org/t/p/w185"
    var genres = movie.genre_ids.joinToString(", ") { genreId ->
        GenreData.genres.first { it.id == genreId }.name
    }
    var color by remember { mutableStateOf(Color.White) }

    Card(
        modifier = Modifier
            .padding(5.dp)
            // Apply onFocusChanged modifier directly to Card
            .onFocusChanged { focusState ->
                color = if (focusState.isFocused) Color.Magenta else Color.White
            }
            .border(5.dp, color, shape = RoundedCornerShape(10))
    ) {
        Column {
            movie.poster_path?.let { posterPath ->
                val imageUrl = "$imageUrlBase$posterPath"
                Image(
                    painter = rememberImagePainter(imageUrl),
                    contentDescription = "Movie Poster",
                    modifier = Modifier
                        .aspectRatio(2 / 3f)
                        .fillMaxWidth()
                        .clickable {
                            viewModel.fetchTrailer(movie.id, "500f402322677a4df10fb559aa63f22b")
                        },
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
                modifier = Modifier.padding(start = 10.dp, top = 0.dp, end = 10.dp, bottom = 20.dp)
            )
        }
    }

    // Observe the video ID LiveData.
    val videoIdEvent by viewModel.videoId.observeAsState()
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
fun TvItemView(tv: TvItem, viewModel: MovieViewModel) {
    val context = LocalContext.current
    val imageUrlBase = "https://image.tmdb.org/t/p/w185"
    var genres = tv.genre_ids.joinToString(", ") { genreId ->
        GenreData.genres.first { it.id == genreId }.name
    }
    var color by remember { mutableStateOf(Color.White) }

    Card(
        modifier = Modifier
            .padding(5.dp)
            // Apply onFocusChanged modifier directly to Card
            .onFocusChanged { focusState ->
                color = if (focusState.isFocused) Color.Magenta else Color.White
            }
            .border(5.dp, color, shape = RoundedCornerShape(10))
    ) {
        Column {
            tv.poster_path?.let { posterPath ->
                val imageUrl = "$imageUrlBase$posterPath"
                Image(
                    painter = rememberImagePainter(imageUrl),
                    contentDescription = "Movie Poster",
                    modifier = Modifier
                        .aspectRatio(2 / 3f)
                        .fillMaxWidth()
                        .clickable {
                            viewModel.fetchTrailer(tv.id, "500f402322677a4df10fb559aa63f22b")
                        },
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
                modifier = Modifier.padding(start = 10.dp, top = 0.dp, end = 10.dp, bottom = 20.dp)
            )
        }
    }

    // Observe the video ID LiveData.
    val videoIdEvent by viewModel.videoId.observeAsState()
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
fun MovieDetailsPage(movie: MovieItem) {
    Column {
        Text(text = movie.title)
        Text(text = "Rating: ${movie.vote_average}")
        Text(text = "Release date: ${movie.release_date}")
        // Add more details as needed
    }
}
