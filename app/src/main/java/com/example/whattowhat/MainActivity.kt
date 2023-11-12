package com.example.whattowhat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.whattowhat.model.Movie
import com.example.whattowhat.model.Provider
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment


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
    val movies = movieViewModel.getPopularMovies("500f402322677a4df10fb559aa63f22b").observeAsState(initial = emptyList())
    val providers = movieViewModel.getMovieProviders("500f402322677a4df10fb559aa63f22b").observeAsState(initial = emptyList())

    LazyColumn {
        item {
            // Put non-scrollable content here if needed
            Text("Header", modifier = Modifier.padding(16.dp))
        }
        items(movies.value) { movie ->
            MovieItem(movie)
        }
        item {
            // Spacer or Divider if you want to separate movies from providers
            Divider()
        }
        item {
            // Spacer or Divider if you want to separate movies from providers
            ProviderList(providers.value)
        }
    }
}

@Composable
fun MovieItem(movie: Movie) {
    // Your movie item UI
    Text(text = movie.title, modifier = Modifier.padding(16.dp))
}

@Composable
fun ProviderList(providers: List<Provider>) {
    var selectedProvider by remember { mutableStateOf<Provider?>(null) }

    Column(Modifier.selectableGroup()) {
        providers.forEach { provider ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (selectedProvider == provider),
                        onClick = { selectedProvider = provider }
                    )
                    .padding(16.dp)
            ) {
                RadioButton(
                    selected = (selectedProvider == provider),
                    onClick = { selectedProvider = provider }
                )
                Text(
                    text = provider.provider_name,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .align(Alignment.CenterVertically)
                )
                // You can add more UI elements here if needed
            }
        }
    }
}

@Composable
fun ProviderItem(provider: Provider) {
    // Your provider item UI
    Text(text = provider.provider_name, modifier = Modifier.padding(16.dp))
    // Plus any additional UI elements for displaying the provider
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MovieApp()
}
