package com.example.Runner.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.Runner.data.model.LatLng
import com.example.Runner.location.LocationData
import com.example.Runner.location.LocationManager
import com.example.Runner.presentation.viewmodel.RouteViewModel
import com.example.Runner.ui.components.LocationLoadingIndicator
import com.example.Runner.ui.components.MapScreen
import com.example.Runner.ui.components.RouteCard
import com.example.Runner.ui.theme.RunnerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteSelectionScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RouteViewModel = hiltViewModel(),
    locationManager: LocationManager = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val routes by viewModel.routes.collectAsState()
    val selectedRoute by viewModel.selectedRoute.collectAsState()
    
    // Get user location
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    
    LaunchedEffect(Unit) {
        locationManager.getCurrentLocation().fold(
            onSuccess = { location ->
                userLocation = LatLng(location.latitude, location.longitude)
            },
            onFailure = { /* Handle error */ }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Your Routes",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.refreshRoutes() },
                        enabled = !uiState.isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh routes"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LocationLoadingIndicator(
                        message = "Generating your personalized routes..."
                    )
                }
            }
            
            uiState.error != null -> {
                ErrorSection(
                    error = uiState.error,
                    onRetry = { viewModel.refreshRoutes() },
                    onDismiss = { viewModel.clearError() }
                )
            }
            
            routes.isEmpty() -> {
                NoRoutesSection(
                    onRetry = { viewModel.refreshRoutes() }
                )
            }
            
            else -> {
                Column(
                    modifier = modifier.fillMaxSize()
                ) {
                    // Map section (top half)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        MapScreen(
                            userLocation = userLocation,
                            routes = routes,
                            selectedRoute = selectedRoute,
                            onRouteClick = { route ->
                                if (selectedRoute?.id == route.id) {
                                    viewModel.clearSelectedRoute()
                                } else {
                                    viewModel.selectRoute(route)
                                }
                            }
                        )
                    }
                    
                    // Route list section (bottom half)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Distance info
                        if (uiState.lastRequestDistance > 0) {
                            Text(
                                text = "Routes for ${uiState.lastRequestDistance} ${uiState.lastRequestUnits.name.lowercase()}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text(
                                    text = "Found ${routes.size} route${if (routes.size != 1) "s" else ""} for you:",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            
                            items(routes) { route ->
                                val isSelected = route.id == selectedRoute?.id
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    border = if (isSelected) {
                                        androidx.compose.foundation.BorderStroke(
                                            2.dp, 
                                            MaterialTheme.colorScheme.primary
                                        )
                                    } else null,
                                    colors = if (isSelected) {
                                        CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                        )
                                    } else CardDefaults.cardColors()
                                ) {
                                    RouteCard(
                                        route = route,
                                        onRouteClick = {
                                            if (isSelected) {
                                                viewModel.clearSelectedRoute()
                                            } else {
                                                viewModel.selectRoute(route)
                                            }
                                        },
                                        onFavoriteClick = {
                                            viewModel.toggleRouteFavorite(route.id)
                                        }
                                    )
                                }
                            }
                            
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorSection(
    error: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Oops! Something went wrong",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Text("Dismiss")
                    }
                    
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text("Try Again")
                    }
                }
            }
        }
    }
}

@Composable
private fun NoRoutesSection(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No Routes Found",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "We couldn't generate routes for your location. Please try a different distance or check your internet connection.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onRetry
        ) {
            Text("Try Again")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RouteSelectionScreenPreview() {
    RunnerTheme {
        RouteSelectionScreen(
            onNavigateBack = { }
        )
    }
}