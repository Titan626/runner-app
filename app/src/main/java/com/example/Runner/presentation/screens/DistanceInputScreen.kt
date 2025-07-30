package com.example.Runner.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.Runner.data.model.DistanceUnit
import com.example.Runner.presentation.viewmodel.RouteViewModel
import com.example.Runner.ui.components.DistanceInputField
import com.example.Runner.ui.components.LocationLoadingIndicator
import com.example.Runner.ui.components.RunnerButton
import com.example.Runner.ui.components.RunnerButtonSize
import com.example.Runner.ui.components.RunnerButtonVariant
import com.example.Runner.ui.theme.RunnerTheme

@Composable
fun DistanceInputScreen(
    onNavigateToRoutes: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RouteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var distance by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf(DistanceUnit.KILOMETERS) }
    var validationError by remember { mutableStateOf<String?>(null) }

    // Validate distance input
    val isValidDistance = remember(distance) {
        distance.toDoubleOrNull()?.let { dist ->
            when (unit) {
                DistanceUnit.KILOMETERS -> dist in 0.1..50.0
                DistanceUnit.MILES -> dist in 0.1..31.0 // ~50km
            }
        } ?: false
    }

    // Update validation error
    LaunchedEffect(distance, unit) {
        validationError = when {
            distance.isBlank() -> null
            distance.toDoubleOrNull() == null -> "Please enter a valid number"
            !isValidDistance -> {
                val maxDistance = if (unit == DistanceUnit.KILOMETERS) "50 km" else "31 miles"
                "Distance must be between 0.1 and $maxDistance"
            }
            else -> null
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // Header
        Text(
            text = "Let's Go Running!",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Enter your target distance and we'll generate personalized routes for you",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Distance input
        DistanceInputField(
            distance = distance,
            onDistanceChange = { distance = it },
            unit = unit,
            onUnitChange = { unit = it },
            isError = validationError != null,
            errorMessage = validationError,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Generate routes button
        if (uiState.isLoading) {
            LocationLoadingIndicator(
                message = "Generating your routes..."
            )
        } else {
            RunnerButton(
                text = "Generate Routes",
                onClick = {
                    if (isValidDistance && distance.isNotBlank()) {
                        val distanceValue = distance.toDouble()
                        viewModel.generateRoutes(distanceValue, unit)
                        onNavigateToRoutes()
                    }
                },
                enabled = isValidDistance && distance.isNotBlank() && !uiState.isLoading,
                variant = RunnerButtonVariant.Primary,
                size = RunnerButtonSize.Large,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }
        
        // Error display
        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Error",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = uiState.error ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { viewModel.clearError() },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        ) {
                            Text("Dismiss")
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Tips section
        DistanceInputTips(unit = unit)
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun DistanceInputTips(
    unit: DistanceUnit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "💡 Tips for Better Routes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val tips = when (unit) {
                DistanceUnit.KILOMETERS -> listOf(
                    "• 1-3km: Perfect for beginners or quick runs",
                    "• 5km: Most popular distance for regular runners",
                    "• 10km+: Challenge distance for experienced runners"
                )
                DistanceUnit.MILES -> listOf(
                    "• 0.5-2 miles: Perfect for beginners or quick runs",
                    "• 3 miles: Most popular distance for regular runners",
                    "• 6+ miles: Challenge distance for experienced runners"
                )
            }
            
            tips.forEach { tip ->
                Text(
                    text = tip,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DistanceInputScreenPreview() {
    RunnerTheme {
        DistanceInputScreen(
            onNavigateToRoutes = { }
        )
    }
}