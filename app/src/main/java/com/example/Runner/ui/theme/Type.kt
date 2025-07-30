package com.example.Runner.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Urbanist Font Family (Note: Font files need to be added to res/font/)
val UrbanistFontFamily = FontFamily.Default // TODO: Replace with actual Urbanist font files

// Athletic Typography with Tailwind CSS sizing convention
val Typography = Typography(
    // text-5xl: 48sp - Display Large
    displayLarge = TextStyle(
        fontFamily = UrbanistFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = (-0.5).sp
    ),
    
    // text-4xl: 36sp - Display Medium  
    displayMedium = TextStyle(
        fontFamily = UrbanistFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.25).sp
    ),
    
    // text-3xl: 30sp - Display Small
    displaySmall = TextStyle(
        fontFamily = UrbanistFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    
    // text-2xl: 24sp - Headline Large
    headlineLarge = TextStyle(
        fontFamily = UrbanistFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    
    // text-xl: 20sp - Headline Medium
    headlineMedium = TextStyle(
        fontFamily = UrbanistFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    
    // text-lg: 18sp - Headline Small
    headlineSmall = TextStyle(
        fontFamily = UrbanistFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    ),
    
    // text-base: 16sp - Body Large
    bodyLarge = TextStyle(
        fontFamily = UrbanistFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    
    // text-sm: 14sp - Body Medium
    bodyMedium = TextStyle(
        fontFamily = UrbanistFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    
    // text-xs: 12sp - Body Small
    bodySmall = TextStyle(
        fontFamily = UrbanistFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    
    // Label styles for buttons and UI elements
    labelLarge = TextStyle(
        fontFamily = UrbanistFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    
    labelMedium = TextStyle(
        fontFamily = UrbanistFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    
    labelSmall = TextStyle(
        fontFamily = UrbanistFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)