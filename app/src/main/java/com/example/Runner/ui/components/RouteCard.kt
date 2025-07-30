package com.example.Runner.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.Runner.data.model.LatLng
import com.example.Runner.data.model.Route
import com.example.Runner.data.model.RouteDifficulty
import com.example.Runner.ui.theme.RunnerTheme

@Composable
fun RouteCard(
    route: Route,
    onRouteClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = onRouteClick,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with title and favorite button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = route.label ?: "Route ${route.id.take(6)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (route.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (route.isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (route.isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Description
            if (!route.description.isNullOrBlank()) {
                Text(
                    text = route.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RouteStatItem(
                    icon = Icons.Default.AccessTime,
                    label = "Distance",
                    value = "${String.format("%.1f", route.distance)} km"
                )
                
                RouteStatItem(
                    icon = Icons.Default.AccessTime,
                    label = "Duration",
                    value = "${route.duration} min"
                )
                
                RouteStatItem(
                    icon = Icons.Default.Landscape,
                    label = "Elevation",
                    value = "${route.elevation} m"
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Difficulty badge
            DifficultyBadge(difficulty = route.difficulty)
        }
    }
}

@Composable
private fun RouteStatItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun DifficultyBadge(
    difficulty: RouteDifficulty,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (difficulty) {
        RouteDifficulty.EASY -> Pair(MaterialTheme.colorScheme.tertiary, "Easy")
        RouteDifficulty.MODERATE -> Pair(MaterialTheme.colorScheme.secondary, "Moderate")
        RouteDifficulty.HARD -> Pair(MaterialTheme.colorScheme.error, "Hard")
    }
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RouteCardPreview() {
    RunnerTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RouteCard(
                route = Route(
                    id = "route123",
                    distance = 5.2,
                    duration = 32,
                    elevation = 120,
                    difficulty = RouteDifficulty.MODERATE,
                    coordinates = emptyList(),
                    startPoint = LatLng(0.0, 0.0),
                    endPoint = LatLng(0.0, 0.0),
                    label = "Scenic Park Loop",
                    description = "A beautiful route through Central Park with moderate hills and scenic views of the city skyline.",
                    isFavorite = false
                ),
                onRouteClick = { },
                onFavoriteClick = { },
                modifier = Modifier.fillMaxWidth()
            )
            
            RouteCard(
                route = Route(
                    id = "route456",
                    distance = 3.8,
                    duration = 18,
                    elevation = 45,
                    difficulty = RouteDifficulty.EASY,
                    coordinates = emptyList(),
                    startPoint = LatLng(0.0, 0.0),
                    endPoint = LatLng(0.0, 0.0),
                    label = "City Sprint",
                    description = "Quick urban route perfect for beginners.",
                    isFavorite = true
                ),
                onRouteClick = { },
                onFavoriteClick = { },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}