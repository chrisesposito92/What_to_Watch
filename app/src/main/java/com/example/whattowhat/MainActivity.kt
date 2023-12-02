package com.example.whattowhat

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.whattowhat.model.NavigationOption
import com.example.whattowhat.model.NavigationOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val movieViewModel: MovieViewModel by viewModels()
            val roomViewModel: RoomViewModel by viewModels()

            val context = LocalContext.current

            // Back press handling
            val onBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (navController.previousBackStackEntry != null) {
                        Log.d("MainActivity", "Going back")
                        // There is a destination to go back to, so navigate up
                        navController.navigateUp()
                    } else {
                        // No destination to go back to, so show exit confirmation
                        MaterialAlertDialogBuilder(context)
                            .setTitle("Exit App")
                            .setMessage("Are you sure you want to exit?")
                            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                            .setPositiveButton("Yes") { _, _ -> finish() }
                            .show()
                    }
                }
            }

            // Register the back press callback
            val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
            onBackPressedDispatcher?.addCallback(this, onBackPressedCallback)

            val items = NavigationOptions.options
            val selectedItem = remember { mutableStateOf<NavigationOption?>(null) }
            val colorScheme = MaterialTheme.colorScheme
            var containerColor by remember { mutableStateOf(colorScheme.primary) }
            var contentColor by remember { mutableStateOf(colorScheme.background) }
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        if(drawerState.isOpen) {
                            Spacer(Modifier.height(12.dp))
                            items.forEach { item ->
                                NavigationDrawerItem(
                                    navItem = item,
                                    selectedItem = selectedItem,
                                    navController = navController,
                                    onSelectedItem = {selectedItem.value = it},
                                    drawerState = drawerState,
                                    scope = scope
                                )
                            }
                        }
                    }
                },
                content = {
                    Row{
                        Column(
                            modifier = Modifier
                                .width(25.dp)
                        ) {
                            TextButton(
                                onClick = { scope.launch { drawerState.open() } },
                                colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                                    contentColor = contentColor,
                                    containerColor = containerColor
                                ),
                                enabled = !drawerState.isOpen,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .onFocusChanged { focusState ->
                                        containerColor = if (focusState.isFocused) colorScheme.onPrimaryContainer else colorScheme.primary
                                        contentColor = if (focusState.isFocused) colorScheme.background else colorScheme.background }
                                    .background(color = MaterialTheme.colorScheme.primary)
                            ) {
                                Column {
                                    "Menu".forEach { character ->
                                        Text(
                                            text = character.toString(),
                                            color = MaterialTheme.colorScheme.background
                                        )
                                    }
                                }
                            }
                        }
                        if (drawerState.isClosed){
                            Log.d("MainActivity", "drawerState is closed")
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                NavHost(
                                    navController = navController,
                                    startDestination = "movietvList"
                                ) {
                                    composable("providerSelection") {
                                        ProviderSelectionScreen(
                                            navController,
                                            drawerState
                                        )
                                    }
                                    composable("movietvList")
                                    {
                                        MovieTvListScreen(
                                            movieViewModel,
                                            navController
                                        )
                                    }
                                    //         composable("home") { MovieTvListScreen(viewModel(), navController) }
                                    composable(
                                        "movieDetail/{movieId}",
                                        arguments = listOf(navArgument("movieId") {
                                            type = NavType.StringType
                                        })
                                    ) { backStackEntry ->
                                        val movieId = backStackEntry.arguments?.getString("movieId")
                                        MovieDetailsPage(
                                            movieId,
                                            movieViewModel,
                                            navController,
                                            roomViewModel
                                        )
                                    }
                                    composable(
                                        "tvDetail/{tvId}",
                                        arguments = listOf(navArgument("tvId") {
                                            type = NavType.StringType
                                        })
                                    ) { backStackEntry ->
                                        val tvId = backStackEntry.arguments?.getString("tvId")
                                        TvDetailsPage(tvId, movieViewModel, navController)
                                    }
                                    composable("watchlist") {
                                        WatchlistScreen(
                                            roomViewModel,
                                            navController
                                        )
                                    }
                                    // Add other composables for different routes as needed
                                    composable("watched") {
                                        WatchedScreen(
                                            roomViewModel,
                                            navController
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawerItem(
    navItem: NavigationOption,
    selectedItem: MutableState<NavigationOption?> = remember { mutableStateOf(null) },
    navController: NavController,
    onSelectedItem: (NavigationOption) -> Unit,
    drawerState: DrawerState,
    scope: CoroutineScope
){
    NavigationDrawerItem(
        icon = { Icon(navItem.iconName, contentDescription = null) },
        label = { Text(navItem.title) },
        selected = navItem == selectedItem.value,
        onClick = {
            navController.navigate(navItem.route)
            onSelectedItem(navItem)
            scope.launch { drawerState.close() }
            selectedItem.value = null
                  },
        modifier = Modifier
            .padding(NavigationDrawerItemDefaults.ItemPadding)

    )
}
