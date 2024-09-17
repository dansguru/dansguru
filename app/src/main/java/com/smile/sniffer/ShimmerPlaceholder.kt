package com.smile.sniffer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import me.vponomarenko.compose.shimmer.shimmer

// Shimmer composable for displaying loading state
@Composable
fun Shimmer() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .shimmer() // Apply shimmer effect here
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    color = Color.Gray,
                    shape = MaterialTheme.shapes.medium
                )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    color = Color.Gray,
                    shape = MaterialTheme.shapes.medium
                )
        )
    }
}