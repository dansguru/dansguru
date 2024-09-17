package com.smile.sniffer.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.camera.view.PreviewView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.smile.sniffer.utils.QRCodeScanner
import com.smile.sniffer.viewmodel.TicketViewModel

@Composable
fun TicketVerificationScreen(
    viewModel: TicketViewModel,
    navController: NavHostController,
    onNavigateToHistory: () -> Unit // Callback for navigation
) {
    val context = LocalContext.current
    val validationStatus by viewModel.validationStatus.collectAsState()
    val qrCodeScanner = remember { QRCodeScanner(viewModel) }

    // Initialize QR Code Scanner
    val previewView = remember { PreviewView(context) }
    LaunchedEffect(Unit) {
        qrCodeScanner.startScan(context, previewView)
    }

    // UI for Ticket Verification Screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Camera Preview
        AndroidView(
            factory = { previewView },
            modifier = Modifier
                .fillMaxSize()
                .background(ComposeColor.Black) // Background color for better contrast
        )

        // Scanning Overlay
        ScannerOverlay(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )

        // Top-right icons for flashlight and history
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            // Flashlight Toggle Icon
            IconButton(
                onClick = { qrCodeScanner.toggleFlashlight() },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = if (qrCodeScanner.isFlashlightOn) Icons.Filled.FlashlightOn else Icons.Filled.FlashlightOff,
                    contentDescription = "Toggle Flashlight",
                    tint = ComposeColor.White
                )
            }

            // History Icon
            IconButton(
                onClick = { onNavigateToHistory() }
            ) {
                Icon(
                    imageVector = Icons.Filled.History,
                    contentDescription = "View Scan History",
                    tint = ComposeColor.White
                )
            }
        }

        // Column for status text
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text(
                text = "Scan QR Code",
                style = MaterialTheme.typography.headlineMedium,
                color = ComposeColor.White, // Text color for better visibility
                modifier = Modifier.padding(bottom = 8.dp) // Add padding for spacing
            )

            Text(
                text = validationStatus ?: "Scan a QR code to see the result",
                style = MaterialTheme.typography.bodyMedium,
                color = ComposeColor.White // Text color for better visibility
            )
        }
    }
}

@Composable
fun ScannerOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ComposeColor.Transparent,
                        ComposeColor.Black.copy(alpha = 0.5f)
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(250.dp)
                .border(2.dp, ComposeColor.White)
                .background(ComposeColor.Transparent)
        )
    }
}
