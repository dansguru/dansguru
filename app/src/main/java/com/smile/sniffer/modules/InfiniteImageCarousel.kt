package com.smile.sniffer.modules

import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.smile.sniffer.api.PictureData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun InfiniteImageCarousel(pictures: List<PictureData>) {
    val pagerState = rememberPagerState { pictures.size }
    var isLoading by remember { mutableStateOf(pictures.isEmpty()) }

    if (pictures.isEmpty()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            color = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tickets are loading ...",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    } else {
        LaunchedEffect(pictures) {
            isLoading = false
        }

        // Auto-slide functionality with reset on pictures change
        LaunchedEffect(pictures) {
            while (true) {
                delay(3000) // Change slide every 3 seconds
                if (pagerState.pageCount > 0) {
                    withContext(Dispatchers.Main) {
                        pagerState.animateScrollToPage((pagerState.currentPage + 1) % pagerState.pageCount)
                    }
                }
            }
        }

        Column {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) { page ->
                val pictureData = pictures[page]

                // State for the extracted color
                var dominantColor by remember { mutableStateOf(Color.Transparent) }

                // Extract the dominant color using the new ExtractDominantColor composable
                ExtractDominantColor(pictureData.bitmap) { color: Color ->
                    dominantColor = color
                }

                // Animate alpha state for transition
                val alpha: Float by animateFloatAsState(
                    targetValue = if (pagerState.currentPage == page) 1f else 0.6f,
                    animationSpec = tween(600), label = ""
                )

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Picture item
                    PictureItem(
                        imageUrl = pictureData.pictureUrl,
                        description = pictureData.expirationDateTime,
                        expirationDateTime = pictureData.expirationDateTime,
                        isLoading = isLoading,
                        modifier = Modifier.alpha(alpha) // Set alpha for fade effect
                    )

                    // Overlay the dominant color with 50% opacity
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(dominantColor.copy(alpha = 0.5f)) // Overlay with extracted dominant color
                    )
                }
            }

            // Pager Indicator for carousel
            PagerIndicator(pagerState = pagerState)
        }
    }
}

@Composable
fun PagerIndicator(pagerState: PagerState) {
    val pageCount = pagerState.pageCount
    val currentPage = pagerState.currentPage

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage

            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(if (isSelected) 12.dp else 8.dp)
                    .alpha(if (isSelected) 1f else 0.5f)
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
            )
        }
    }
}
