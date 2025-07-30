package com.example.Runner.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.Runner.data.model.LatLng
import com.example.Runner.data.model.Route
import com.example.Runner.ui.theme.RunnerTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng as GoogleLatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*

@Composable
fun MapScreen(
    userLocation: LatLng?,
    routes: List<Route> = emptyList(),
    selectedRoute: Route? = null,
    onRouteClick: (Route) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Default camera position (San Francisco if no user location)
    val defaultLocation = GoogleLatLng(37.7749, -122.4194)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            userLocation?.toGoogleLatLng() ?: defaultLocation,
            15f
        )
    }

    // Update camera when user location changes
    LaunchedEffect(userLocation) {
        userLocation?.let { location ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(location.toGoogleLatLng(), 15f),
                1000
            )
        }
    }

    // Update camera to fit all routes when routes change
    LaunchedEffect(routes) {
        if (routes.isNotEmpty()) {
            val boundsBuilder = LatLngBounds.Builder()
            
            routes.forEach { route ->
                route.coordinates.forEach { coord ->
                    boundsBuilder.include(coord.toGoogleLatLng())
                }
            }
            
            try {
                val bounds = boundsBuilder.build()
                val padding = 100 // pixels
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(bounds, padding),
                    1000
                )
            } catch (e: Exception) {
                // Handle case where bounds are invalid
            }
        }
    }

    Box(modifier = modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = false, // We'll handle location ourselves
                mapStyleOptions = null
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = false,
                mapToolbarEnabled = false
            )
        ) {
            // User location marker
            userLocation?.let { location ->
                Marker(
                    state = MarkerState(position = location.toGoogleLatLng()),
                    title = "Your Location",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                )
            }

            // Route polylines
            routes.forEachIndexed { index, route ->
                val isSelected = route.id == selectedRoute?.id
                val routeColor = getRouteColor(index, isSelected)
                val routeWidth = if (isSelected) 8f else 5f
                
                if (route.coordinates.isNotEmpty()) {
                    Polyline(
                        points = route.coordinates.map { it.toGoogleLatLng() },
                        color = routeColor,
                        width = routeWidth,
                        clickable = true,
                        onClick = { onRouteClick(route) }
                    )
                    
                    // Start marker
                    route.coordinates.firstOrNull()?.let { startPoint ->
                        Marker(
                            state = MarkerState(position = startPoint.toGoogleLatLng()),
                            title = route.label ?: "Route ${index + 1}",
                            snippet = "${route.distance} km • ${route.duration} min",
                            icon = BitmapDescriptorFactory.defaultMarker(
                                when (index % 3) {
                                    0 -> BitmapDescriptorFactory.HUE_GREEN
                                    1 -> BitmapDescriptorFactory.HUE_ORANGE
                                    else -> BitmapDescriptorFactory.HUE_VIOLET
                                }
                            ),
                            onClick = {
                                onRouteClick(route)
                                false // Don't consume the event
                            }
                        )
                    }
                }
            }
        }

        // Map overlay controls
        if (userLocation != null) {
            FloatingActionButton(
                onClick = {
                    // Center on user location
                    userLocation.let { location ->
                        cameraPositionState.move(
                            CameraUpdateFactory.newLatLngZoom(location.toGoogleLatLng(), 16f)
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = "📍",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

private fun LatLng.toGoogleLatLng(): GoogleLatLng {
    return GoogleLatLng(this.latitude, this.longitude)
}

private fun getRouteColor(index: Int, isSelected: Boolean): Color {
    val colors = listOf(
        Color(0xFF4CAF50), // Green
        Color(0xFFFF9800), // Orange
        Color(0xFF9C27B0), // Purple
        Color(0xFF2196F3), // Blue
        Color(0xFFFF5722)  // Deep Orange
    )
    
    val baseColor = colors[index % colors.size]
    return if (isSelected) {
        baseColor
    } else {
        baseColor.copy(alpha = 0.7f)
    }
}

@Composable
fun MapLoadingState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Loading map...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MapErrorState(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Map Error",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Retry")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    RunnerTheme {
        MapScreen(
            userLocation = LatLng(37.7749, -122.4194),
            routes = emptyList()
        )
    }
}