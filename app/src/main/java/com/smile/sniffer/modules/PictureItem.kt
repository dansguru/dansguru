package com.smile.sniffer.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.smile.sniffer.Shimmer
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PictureItem(
    imageUrl: String,
    description: String,
    expirationDateTime: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    var remainingTime by remember { mutableStateOf("Calculating...") }
    var descriptionOffset by remember { mutableStateOf(0.dp) }

    // Update countdown every second
    LaunchedEffect(expirationDateTime) {
        while (true) {
            val expirationDateTimeParsed = LocalDateTime.parse(expirationDateTime, dateTimeFormatter)
            remainingTime = calculateRemainingTime(expirationDateTimeParsed)
            delay(1000L) // Update every second
        }
    }

    // Handle the sliding animation of the description
    LaunchedEffect(Unit) {
        while (true) {
            descriptionOffset = (-200).dp // Adjust based on the width of the description
            delay(3000L) // Pause for 3 seconds
            descriptionOffset = 0.dp // Reset position
            delay(3000L) // Continue with a pause
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        if (isLoading) {
            Shimmer() // Ensure Shimmer effect is correctly applied
        } else {
            Box {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                )

                // Countdown Timer
                Text(
                    text = remainingTime,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                )
            }

            // Display the description with sliding animation
            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
                    .background(Color.Black)
                    .offset(x = descriptionOffset)
            ) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                )
            }
        }
    }
}

// Calculate remaining time
fun calculateRemainingTime(expirationDateTime: LocalDateTime): String {
    val now = LocalDateTime.now()
    val duration = java.time.Duration.between(now, expirationDateTime)

    return if (duration.isNegative) {
        "Expired"
    } else {
        val seconds = duration.seconds % 60
        val minutes = (duration.seconds / 60) % 60
        val hours = (duration.seconds / 3600) % 24
        val days = duration.toDays()

        String.format(Locale.getDefault(), "%d days %02d:%02d:%02d", days, hours, minutes, seconds)
    }
}

// Convert Dp to Sp
@Composable
fun Dp.toSp(): TextUnit {
    val density = LocalDensity.current.density
    return (this.value / density).sp
}
