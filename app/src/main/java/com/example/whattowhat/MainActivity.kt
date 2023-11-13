
package com.example.whattowhat

import android.os.Bundle
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


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MovieApp(navController)
                    }
                    composable(
                        "providerDetail/{providerId}/{providerName}",
                        arguments = listOf(
                            navArgument("providerId") { type = NavType.StringType },
                            navArgument("providerName") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val providerId = backStackEntry.arguments?.getString("providerId")
                        val providerName = backStackEntry.arguments?.getString("providerName")
                        ProviderDetailScreen(providerId, providerName)
                    }
                }
        }
    }
}

@Composable
fun MovieApp(navController: NavController, movieViewModel: MovieViewModel = viewModel()) {
    val providers = movieViewModel.getMovieProviders("500f402322677a4df10fb559aa63f22b").observeAsState(initial = emptyList())

    ProviderList(providers.value) { selectedProvider ->
        navController.navigate("providerDetail/${selectedProvider.provider_id}/${selectedProvider.provider_name}")

    }
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
fun ProviderDetailScreen(providerId: String?, providerName: String?, movieViewModel: MovieViewModel = viewModel()) {
    var expanded by remember { mutableStateOf(false) }
    var selectedGenre by remember { mutableStateOf(GenreData.genres.first().name) }
    val movies by movieViewModel.getMoviesByProvider("API_KEY", providerId ?: "").observeAsState(initial = listOf())

    Column {
        Text(text = "Movies on $providerName")

        // Dropdown menu for selecting a genre
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                readOnly = true,
                value = selectedGenre,
                onValueChange = { /* Handle value change if needed */ },
                label = { Text("Select Genre") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Dropdown Arrow"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                GenreData.genres.forEach { genre ->
                    DropdownMenuItem(
                        onClick = {
                            selectedGenre = genre.name
                            expanded = false
                            // Fetch movies based on the selected genre
                        }
                    ) {
                        Text(genre.name)
                    }
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
    // Composable to display individual movie items
    // Include movie title, image, etc.
    Text(text = movie.title)
}
