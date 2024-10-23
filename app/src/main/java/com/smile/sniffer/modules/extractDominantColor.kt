package com.smile.sniffer.modules

import android.graphics.Bitmap
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.palette.graphics.Palette

@Composable
fun ExtractDominantColor(bitmap: Bitmap?, onColorExtracted: (Color) -> Unit) {
    // Initial state for dominant color
    var dominantColor by remember { mutableStateOf(Color.Transparent) }
    val fallbackColor = MaterialTheme.colorScheme.primary // Fallback to theme's primary color

    // Listen to changes in the bitmap and extract color when it's updated
    LaunchedEffect(bitmap) {
        if (bitmap != null) {
            // Process the bitmap and extract colors
            Palette.from(bitmap).generate { palette ->
                val dominantSwatch = palette?.dominantSwatch
                val color = dominantSwatch?.rgb ?: fallbackColor.toArgb() // Use fallback color if no dominant swatch
                dominantColor = Color(color) // Set the extracted color
            }
        } else {
            // Fallback in case bitmap is null
            dominantColor = fallbackColor
        }
        // Notify the caller with the extracted (or fallback) color
        onColorExtracted(dominantColor)
    }
}
