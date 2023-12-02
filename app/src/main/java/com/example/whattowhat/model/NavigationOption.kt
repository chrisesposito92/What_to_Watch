package com.example.whattowhat.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationOption(
    val iconName: ImageVector = Icons.Default.Favorite,
    val title: String,
    val route: String
)