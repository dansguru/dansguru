package com.smile.sniffer.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.unit.dp
import com.smile.sniffer.utils.QRCodeScanner
import com.smile.sniffer.viewmodel.TicketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanHistoryScreen(
    viewModel: TicketViewModel,
    qrCodeScanner: QRCodeScanner, // Pass the scanner instance
    onNavigateBack: () -> Unit
) {
    // Fetch scanned codes using qrCodeScanner.getScannedCodes()
    val scannedCodes = remember { qrCodeScanner.getScannedCodes() }
    var searchQuery by remember { mutableStateOf("") }

    val filteredCodes = scannedCodes.filter { it.contains(searchQuery, ignoreCase = true) }
    // UI for Scan History Screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top bar with Back button and Clear History button
        TopAppBar(
            title = { Text("Scan History") },
            navigationIcon = {
                IconButton(onClick = { onNavigateBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ComposeColor.White
                    )
                }
            },
            actions = {
                IconButton(onClick = { viewModel.clearScannedCodes() }) {
                    Icon(
                        imageVector = Icons.Filled.DeleteForever,
                        contentDescription = "Clear History",
                        tint = ComposeColor.White
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(ComposeColor.Gray.copy(alpha = 0.1f), shape = MaterialTheme.shapes.medium),
            label = { Text("Search") },
            placeholder = { Text("Enter code to search") }, // Hint text
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "Clear",
                            tint = ComposeColor.Gray
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // History list
        LazyColumn {
            items(filteredCodes) { code ->
                ScanHistoryItem(
                    code = code,
                    onDelete = { codeToDelete ->
                        viewModel.removeScannedCode(codeToDelete)
                    }
                )
            }
        }
    }
}

@Composable
fun ScanHistoryItem(
    code: String,
    onDelete: (String) -> Unit // Callback for deletion
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = code,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    onDelete(code) // Invoke the deletion callback
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete", color = ComposeColor.White)
            }
        }
    }
}
