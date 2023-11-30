
package com.example.whattowhat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
            val movieViewModel: MovieViewModel by viewModels()
            val roomViewModel: RoomViewModel by viewModels()
            // Observe the watchlist LiveData
//            val watchlist by roomViewModel.watchlist.observeAsState(initial = emptyList())

            NavHost(navController = navController, startDestination = "providerSelection") {
                composable("providerSelection") { ProviderSelectionScreen(navController) }
                composable("movietvList/{selectedProviders}", arguments = listOf(navArgument("selectedProviders") { type = NavType.StringType })) { backStackEntry ->
                    val selectedProviders = backStackEntry.arguments?.getString("selectedProviders")?.split(",")?.map { it.toInt() }
                    MovieTvListScreen(movieViewModel, navController, selectedProviders, roomViewModel)
                }
       //         composable("home") { MovieTvListScreen(viewModel(), navController) }
                composable("movieDetail/{movieId}", arguments = listOf(navArgument("movieId") { type = NavType.StringType })) { backStackEntry ->
                    val movieId = backStackEntry.arguments?.getString("movieId")
                    MovieDetailsPage(movieId, movieViewModel, navController, roomViewModel)
                }
                composable("tvDetail/{tvId}", arguments = listOf(navArgument("tvId") { type = NavType.StringType })) { backStackEntry ->
                    val tvId = backStackEntry.arguments?.getString("tvId")
                    TvDetailsPage(tvId, movieViewModel, navController)
                }
                composable("watchlist") { WatchlistScreen(roomViewModel, navController) }
                // Add other composables for different routes as needed
            }
        }

    }
}