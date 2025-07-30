package com.example.Runner.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.Runner.ui.theme.RunnerTheme

@Composable
fun RunnerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: RunnerButtonVariant = RunnerButtonVariant.Primary,
    size: RunnerButtonSize = RunnerButtonSize.Large
) {
    val buttonColors = when (variant) {
        RunnerButtonVariant.Primary -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
        )
        RunnerButtonVariant.Secondary -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
            disabledContentColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f)
        )
        RunnerButtonVariant.Outline -> ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
    }

    val buttonHeight = when (size) {
        RunnerButtonSize.Small -> 40.dp
        RunnerButtonSize.Medium -> 48.dp
        RunnerButtonSize.Large -> 56.dp
    }

    val textStyle = when (size) {
        RunnerButtonSize.Small -> MaterialTheme.typography.labelMedium
        RunnerButtonSize.Medium -> MaterialTheme.typography.labelLarge
        RunnerButtonSize.Large -> MaterialTheme.typography.labelLarge
    }

    when (variant) {
        RunnerButtonVariant.Outline -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier.height(buttonHeight),
                enabled = enabled,
                colors = buttonColors,
                shape = RoundedCornerShape(24.dp),
                border = ButtonDefaults.outlinedButtonBorder(
                    enabled = enabled
                ).copy(width = 2.dp)
            ) {
                Text(
                    text = text,
                    style = textStyle,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        else -> {
            Button(
                onClick = onClick,
                modifier = modifier.height(buttonHeight),
                enabled = enabled,
                colors = buttonColors,
                shape = RoundedCornerShape(24.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 8.dp,
                    disabledElevation = 0.dp
                )
            ) {
                Text(
                    text = text,
                    style = textStyle,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

enum class RunnerButtonVariant {
    Primary, Secondary, Outline
}

enum class RunnerButtonSize {
    Small, Medium, Large
}

@Preview(showBackground = true)
@Composable
fun RunnerButtonPreview() {
    RunnerTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RunnerButton(
                text = "Start Running",
                onClick = { },
                variant = RunnerButtonVariant.Primary,
                size = RunnerButtonSize.Large,
                modifier = Modifier.fillMaxWidth()
            )
            
            RunnerButton(
                text = "Save Route",
                onClick = { },
                variant = RunnerButtonVariant.Secondary,
                size = RunnerButtonSize.Medium,
                modifier = Modifier.fillMaxWidth()
            )
            
            RunnerButton(
                text = "View Details",
                onClick = { },
                variant = RunnerButtonVariant.Outline,
                size = RunnerButtonSize.Small,
                modifier = Modifier.fillMaxWidth()
            )
            
            RunnerButton(
                text = "Disabled",
                onClick = { },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}