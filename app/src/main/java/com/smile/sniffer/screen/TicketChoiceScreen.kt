package com.smile.sniffer.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.smile.sniffer.R

@Composable
fun TicketChoiceScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray) // Fallback color for loading state
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.split),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(10.dp),
            contentScale = ContentScale.Crop
        )

        // Overlay with blur effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )

        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Create a New Ticket",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Elevated Card for Buttons
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.elevatedCardElevation(4.dp), // Card elevation
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Upload Illustration Button
                    ElevatedButton(
                        onClick = { navController.navigate("illustrated_ticket") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(imageVector = Icons.Filled.ImageSearch, contentDescription = "Upload Illustration")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Upload Illustration")
                    }

                    // Fill Fields Manually Button
                    ElevatedButton(
                        onClick = { navController.navigate("manual_entry") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(imageVector = Icons.Filled.Checklist, contentDescription = "Fill Fields Manually")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Fill Fields Manually")
                    }
                }
            }
        }
    }
}
