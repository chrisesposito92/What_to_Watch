package com.example.whattowhat.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search

object NavigationOptions {
    val options = listOf(
        NavigationOption(
            Icons.Default.Home,
            title = "Home",
            route = "movietvList"
        ),
        NavigationOption(
            Icons.Default.Favorite,
            title = "Watchlist",
            route = "watchlist"
        ),
        NavigationOption(
            Icons.Default.CheckBox,
            title = "Watched",
            route = "watched"
        ),
        NavigationOption(
            Icons.Default.Contacts,
            title = "Edit Providers",
            route = "providerSelection"
        ),
        NavigationOption(
            Icons.Default.Search,
            title = "Search",
            route = "search"
        ),
        NavigationOption(
            Icons.Default.Android,
            title = "GPT",
            route = "gpt"
        )
    )
}