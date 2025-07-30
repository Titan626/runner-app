package com.example.Runner.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.Runner.presentation.screens.DistanceInputScreen
import com.example.Runner.presentation.screens.FavoritesScreen
import com.example.Runner.presentation.screens.HomeScreen
import com.example.Runner.presentation.screens.ProfileScreen
import com.example.Runner.presentation.screens.RouteSelectionScreen

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Run : Screen("run", "Run", Icons.Default.DirectionsRun)
    object Favorites : Screen("favorites", "Favorites", Icons.Default.Favorite)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    
    // Non-tab screens
    object DistanceInput : Screen("distance_input", "Distance Input", Icons.Default.DirectionsRun)
    object RouteSelection : Screen("route_selection", "Route Selection", Icons.Default.DirectionsRun)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Run,
    Screen.Favorites,
    Screen.Profile
)

@Composable
fun RunnerNavigation() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToRun = {
                        navController.navigate(Screen.DistanceInput.route)
                    }
                )
            }
            
            composable(Screen.Run.route) {
                DistanceInputScreen(
                    onNavigateToRoutes = {
                        navController.navigate(Screen.RouteSelection.route)
                    }
                )
            }
            
            composable(Screen.Favorites.route) {
                FavoritesScreen()
            }
            
            composable(Screen.Profile.route) {
                ProfileScreen()
            }
            
            // Non-tab screens
            composable(Screen.DistanceInput.route) {
                DistanceInputScreen(
                    onNavigateToRoutes = {
                        navController.navigate(Screen.RouteSelection.route)
                    }
                )
            }
            
            composable(Screen.RouteSelection.route) {
                RouteSelectionScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}