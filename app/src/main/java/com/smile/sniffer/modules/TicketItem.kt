package com.smile.sniffer.modules

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smile.sniffer.model.Ticket
import kotlinx.coroutines.delay
import java.time.LocalTime
import androidx.compose.ui.util.lerp
import com.smile.sniffer.model.ShareButton

@Composable
fun TicketItem(ticket: Ticket?, isLoading: Boolean, onClick: () -> Unit, onPaymentClick: () -> Unit) {
    if (isLoading || ticket == null) {
        // Show shimmer effect while loading
        ShimmerEffect()
    } else {
        // Render actual ticket details
        val currentTime = LocalTime.now()
        val isDayTime = currentTime.hour in 6..18

        val gradientColors = if (isDayTime) {
            listOf(Color(0xFF910928), Color(0xE8000000)) // Morning gradient
        } else {
            listOf(Color(0xFF001F3F), Color(0xFF4A148C)) // Evening/Night gradient
        }

        // Animated scale state
        var isExpanded by remember { mutableStateOf(false) }
        val cardScale by animateFloatAsState(
            targetValue = if (isExpanded) 1.05f else 1f,
            animationSpec = tween(durationMillis = 300), label = ""
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onClick()
                        isExpanded = !isExpanded // Toggle card size on click
                    }
                    .graphicsLayer(
                        scaleX = cardScale,
                        scaleY = cardScale
                    ),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(Brush.linearGradient(gradientColors))
                        .padding(20.dp)
                        .fillMaxWidth()
                ) {
                    NeonTitle(text = ticket.title)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = ticket.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    PrizeDetail(prize = ticket.prize)
                    EventTimeDetail(eventTime = ticket.eventTime)
                    TicketDetail(label = "Place", value = ticket.place)
                    TicketDetail(label = "Open Time", value = ticket.openTime)
                    TicketDetail(label = "Close Time", value = ticket.closeTime)
                    TicketDetail(label = "Type", value = ticket.ticketType)
                    TicketDetail(label="Expiration Date",value=ticket.expirationDate)
                    TicketDetail(label = "Attire Style", value = ticket.attireStyle)


                    ShareButton(contentToShare = ticket.title)
                }
            }

            // Floating Payment Button
            IconButton(
                onClick = { onPaymentClick() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .background(Color.Transparent)
            ) {
                Icon(
                    imageVector = Icons.Filled.Payment,
                    contentDescription = "Pay for Ticket",
                    tint = Color.White
                )
            }
        }
    }
}


// Neon-style animated title
@Composable
fun NeonTitle(text: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val glow by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 46.sp
        ),
        color = Color.White.copy(alpha = lerp(0.8f, 5.0f, glow)),
        textAlign = TextAlign.Center,
        modifier = Modifier.graphicsLayer {
            scaleX = glow
            scaleY = glow
        }
    )
}

// Prize detail with a subtle bounce animation
@Composable
fun PrizeDetail(prize: String) {
    var scale by remember { mutableFloatStateOf(1f) }
    LaunchedEffect(Unit) {
        while (true) {
            scale = 1.2f
            delay(300)
            scale = 1f
            delay(300)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "💰 $prize",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFFFFC700) // Golden money color
            ),
            modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
        )
    }
}

// Event time detail with a time feel (clock icon and gradient)
@Composable
fun EventTimeDetail(eventTime: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⏰ $eventTime",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                color = Color(0xFF00B4D8), // Use a single color instead of gradient
                fontSize = 16.sp
            )
        )
    }
}



// General Ticket Detail (place, open/close time, etc.)
@Composable
fun TicketDetail(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = "$label: $value",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 2f),
                fontSize = 16.sp
            )
        )
    }
}

// Add Shimmer Placeholder for loading
@Composable
fun ShimmerEffect() {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val shimmerTranslateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(animation = tween(1000)), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = shimmerColors,
                    start = Offset(shimmerTranslateAnim, 0f),
                    end = Offset(shimmerTranslateAnim + 300f, 0f)
                )
            )
    )
}

