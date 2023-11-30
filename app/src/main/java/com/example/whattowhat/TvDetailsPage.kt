package com.example.whattowhat

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun TvDetailsPage(tvId: String?, movieViewModel: MovieViewModel, navController: NavController) {
    Column {
        Text(text = tvId.toString() )
        // Add more details as needed
    }
}