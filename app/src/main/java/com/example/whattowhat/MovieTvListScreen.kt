package com.example.whattowhat

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.whattowhat.model.MovieGenreData
import com.example.whattowhat.model.ProviderData
import com.example.whattowhat.model.SortOptions
import com.example.whattowhat.model.TvGenreData
import com.example.whattowhat.model.Years

@SuppressLint("SuspiciousIndentation")
@Composable
fun MovieTvListScreen(movieViewModel: MovieViewModel = viewModel(), navController: NavController, roomViewModel: RoomViewModel){

    var isMoviesSelected by remember { mutableStateOf(true) }
    var genres by remember { mutableStateOf(MovieGenreData.genres) }
    val years = Years.years
    val ratings = (1 .. 10).toList()
    val selectedProviderIds =  RememberProviders().getSelectedProviders(LocalContext.current).map { it.toInt() }
    val providers = ProviderData.providers.filter { it.provider_id == 0 || it.provider_id in selectedProviderIds.orEmpty() }
    val rememberedRating = RememberFilters().getMinRating(LocalContext.current)
    var selectedRating by remember { mutableIntStateOf(rememberedRating) }
    val rememberedYear = RememberFilters().getSelectedYear(LocalContext.current)
    var selectedYear by remember { mutableStateOf(rememberedYear) }
    val rememberedGenre = RememberFilters().getSelectedGenre(LocalContext.current)
    var selectedGenreId by remember { mutableIntStateOf(rememberedGenre) }
    val rememberedProviderId = RememberFilters().getSelectedProvider(LocalContext.current)
    var selectedProviderId by remember { mutableIntStateOf(rememberedProviderId) }
    val movies by movieViewModel.moviesState.observeAsState(initial = emptyList())
    val tv by movieViewModel.tvState.observeAsState(initial = emptyList())
    var currentPage by remember { mutableIntStateOf(1) }
    val totalPagesMovie by movieViewModel.totalPagesMovie.observeAsState(1)
    val totalPagesTv by movieViewModel.totalPagesTv.observeAsState(1)
    var totalPages by remember { mutableIntStateOf(1) }
    val sorts = SortOptions.sortOptions
    val rememberedSort = RememberFilters().getSelectedSortOption(LocalContext.current)
    var selectedSortId by remember { mutableStateOf(rememberedSort) }
    val rememberedIncludeAnimation = RememberFilters().getIncludeAnimation(LocalContext.current)
    var excludeAnimation by remember { mutableStateOf(rememberedIncludeAnimation) }
    val gridState = rememberLazyGridState()
    var filtersVisible by remember { mutableStateOf(true) }
    val context = LocalContext.current

    Log.e("MovieViewModel", "REMEMBERED SORT: $rememberedSort")
    Log.e("MovieViewModel", "REMEMBERED GENRE: $rememberedGenre")

    /*
    LaunchedEffect(isMoviesSelected) {
        genres = if (isMoviesSelected) {
            MovieGenreData.genres
        } else {
            TvGenreData.genres
        }

        // Reset the selected genre when switching between Movies and TV
        selectedGenreId = genres.first().id
    }

     */


    // Fetch movies when currentPage changes
    LaunchedEffect(
        currentPage,
        selectedGenreId,
        selectedProviderId,
        selectedSortId,
        excludeAnimation,
        selectedYear,
        selectedRating,
        isMoviesSelected
    ) {
        val genreId = selectedGenreId.toString()

        val providerId = if(selectedProviderId == -1) selectedProviderIds?.joinToString("|") else selectedProviderId.toString()
        Log.e("MovieViewModel", "PROVIDER ID: $providerId")

        Log.e("MovieViewModel", "GENRE ID: $genreId")
        if(isMoviesSelected){
            movieViewModel.getMovies(
                apiKey = "500f402322677a4df10fb559aa63f22b",
                genreId = if(selectedGenreId == 0) null else selectedGenreId.toString(),
                providerId = if(selectedProviderId == 0) selectedProviderIds?.joinToString("|") else selectedProviderId.toString(),
                year = if (selectedYear == "All Years") null else selectedYear.toInt(),
                voteAverage = if (selectedRating == 0) null else selectedRating,
                page = currentPage,
                sortBy = selectedSortId,
                excludeAnimation = excludeAnimation
            )
        }else{
            movieViewModel.getTV(
                apiKey = "500f402322677a4df10fb559aa63f22b",
                genreId = if(selectedGenreId == 0) null else selectedGenreId.toString(),
                providerId = if(selectedProviderId == -1) selectedProviderIds?.joinToString("|") else selectedProviderId.toString(),
                year = if (selectedYear == "All Years") null else selectedYear.toInt(),
                voteAverage = if (selectedRating == 0) null else selectedRating,
                page = currentPage,
                sortBy = selectedSortId,
                excludeAnimation = excludeAnimation
            )
        }
    }


    Column(
        modifier = Modifier
            .padding(8.dp)
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
                providers = providers,
                selectedProvider = selectedProviderId,
                onProviderSelected = { selectedProviderId = it },
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
                onClick = { filtersVisible = !filtersVisible }

            ) {
                Text(if (filtersVisible) "Hide Filters" else "Show Filters")
            }
            Button(
                onClick = {
                    RememberFilters().saveSelectedGenre(context, 0)
                    selectedGenreId = 0
                    RememberFilters().saveSelectedYear(context, "All Years")
                    selectedYear = "All Years"
                    RememberFilters().saveIncludeAnimation(context, false)
                    excludeAnimation = false
                    RememberFilters().saveMinRating(context, 1)
                    selectedRating = 1
                    RememberFilters().saveSelectedSortOption(context, "popularity.desc")
                    selectedSortId = "popularity.desc"
                    RememberFilters().saveSelectedProviderId(context, 0)
                    selectedProviderId = 0
                }
            ){
                Text("Reset Filters")
            }
        }

        if (isMoviesSelected) {
            totalPages = totalPagesMovie
            Column (
                modifier = Modifier
                    .padding(start = 4.dp, top = 0.dp, end = 0.dp, bottom = 0.dp)
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
                        columns = GridCells.Fixed(8)
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
