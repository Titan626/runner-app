package com.example.Runner.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.Runner.data.model.DistanceUnit
import com.example.Runner.ui.theme.RunnerTheme

@Composable
fun DistanceInputField(
    distance: String,
    onDistanceChange: (String) -> Unit,
    unit: DistanceUnit,
    onUnitChange: (DistanceUnit) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Target Distance",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Distance input field
            OutlinedTextField(
                value = distance,
                onValueChange = onDistanceChange,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                isError = isError,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    errorBorderColor = MaterialTheme.colorScheme.error
                ),
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                placeholder = {
                    Text(
                        text = "5.0",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
            
            // Unit selector
            UnitSelector(
                selectedUnit = unit,
                onUnitChange = onUnitChange,
                modifier = Modifier.width(100.dp)
            )
        }
        
        // Error message
        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
        
        // Distance suggestions
        Spacer(modifier = Modifier.height(16.dp))
        DistanceSuggestions(
            unit = unit,
            onSuggestionClick = { suggestionDistance ->
                onDistanceChange(suggestionDistance.toString())
            }
        )
    }
}

@Composable
private fun UnitSelector(
    selectedUnit: DistanceUnit,
    onUnitChange: (DistanceUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(4.dp)
        ) {
            DistanceUnit.values().forEach { unit ->
                val isSelected = unit == selectedUnit
                
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    onClick = { onUnitChange(unit) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = unit.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DistanceSuggestions(
    unit: DistanceUnit,
    onSuggestionClick: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val suggestions = when (unit) {
        DistanceUnit.KILOMETERS -> listOf(1.0, 2.5, 5.0, 7.5, 10.0)
        DistanceUnit.MILES -> listOf(0.5, 1.0, 3.0, 5.0, 6.0)
    }
    
    Column(modifier = modifier) {
        Text(
            text = "Quick Select",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            suggestions.forEach { suggestion ->
                SuggestionChip(
                    distance = suggestion,
                    unit = unit,
                    onClick = { onSuggestionClick(suggestion) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SuggestionChip(
    distance: Double,
    unit: DistanceUnit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val unitText = when (unit) {
        DistanceUnit.KILOMETERS -> "km"
        DistanceUnit.MILES -> "mi"
    }
    
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = null
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${if (distance % 1.0 == 0.0) distance.toInt() else distance}$unitText",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DistanceInputFieldPreview() {
    RunnerTheme {
        var distance by remember { mutableStateOf("5.0") }
        var unit by remember { mutableStateOf(DistanceUnit.KILOMETERS) }
        
        DistanceInputField(
            distance = distance,
            onDistanceChange = { distance = it },
            unit = unit,
            onUnitChange = { unit = it },
            isError = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DistanceInputFieldErrorPreview() {
    RunnerTheme {
        var distance by remember { mutableStateOf("100") }
        var unit by remember { mutableStateOf(DistanceUnit.KILOMETERS) }
        
        DistanceInputField(
            distance = distance,
            onDistanceChange = { distance = it },
            unit = unit,
            onUnitChange = { unit = it },
            isError = true,
            errorMessage = "Distance must be between 0.1 and 50 km",
            modifier = Modifier.padding(16.dp)
        )
    }
}