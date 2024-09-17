package com.smile.sniffer.model

import android.content.Intent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun ShareButton(contentToShare: String) {
    val context = LocalContext.current

    // Animated icon scale
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val iconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    // Button click scale animation
    var buttonScale by remember { mutableFloatStateOf(1f) }
    val scaleAnimation by animateFloatAsState(
        targetValue = buttonScale,
        animationSpec = tween(durationMillis = 150), label = ""
    )

    // Reset button scale after animation
    val resetButtonScale by rememberUpdatedState(buttonScale)

    Box(
        modifier = Modifier
            .padding(16.dp)
            .graphicsLayer(scaleX = scaleAnimation, scaleY = scaleAnimation)
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .size(48.dp)
            .clickable {
                buttonScale = 0.9f
                // Share content
                shareContent(context, contentToShare)
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Share,
            contentDescription = "Share Event",
            tint = Color.White,
            modifier = Modifier.scale(iconScale)
        )
    }

    // Apply animation to reset button scale
    LaunchedEffect(resetButtonScale) {
        if (resetButtonScale != 1f) {
            delay(150)
            buttonScale = 1f
        }
    }
}

private fun shareContent(context: android.content.Context, content: String) {
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, content)
        type = "text/plain"
    }
    val chooserIntent = Intent.createChooser(shareIntent, "Share via")
    context.startActivity(chooserIntent)
}
