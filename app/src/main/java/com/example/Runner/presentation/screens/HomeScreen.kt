package com.example.Runner.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.Runner.ui.components.RunnerButton
import com.example.Runner.ui.components.RunnerButtonSize
import com.example.Runner.ui.components.RunnerButtonVariant
import com.example.Runner.ui.theme.RunnerTheme

@Composable
fun HomeScreen(
    onNavigateToRun: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // Welcome header
        Text(
            text = "Welcome to Runner",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Generate personalized running routes with AI-powered descriptions",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Main CTA
        RunnerButton(
            text = "Start Running",
            onClick = onNavigateToRun,
            variant = RunnerButtonVariant.Primary,
            size = RunnerButtonSize.Large,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Feature cards
        Text(
            text = "What you can do",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FeatureCard(
                icon = Icons.Default.DirectionsRun,
                title = "Smart Route Generation",
                description = "Get 2-3 personalized running routes based on your target distance and current location"
            )
            
            FeatureCard(
                icon = Icons.Default.TrendingUp,
                title = "AI-Powered Descriptions",
                description = "Each route comes with intelligent labels and descriptions powered by Claude AI"
            )
            
            FeatureCard(
                icon = Icons.Default.AccessTime,
                title = "Save Your Favorites",
                description = "Keep track of your best routes and access them anytime, even offline"
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Quick stats or tips
        QuickTipsCard()
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun FeatureCard(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun QuickTipsCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "💡 Quick Tips",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val tips = listOf(
                "Start with shorter distances if you're new to running",
                "Routes are cached for 24 hours for faster loading",
                "Each route shows elevation, duration, and difficulty",
                "Save your favorite routes for quick access"
            )
            
            tips.forEach { tip ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(
                        text = "• ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    RunnerTheme {
        HomeScreen(
            onNavigateToRun = { }
        )
    }
}