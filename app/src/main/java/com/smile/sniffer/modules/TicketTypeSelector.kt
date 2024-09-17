package com.smile.sniffer.modules

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign

@Composable
fun TicketTypeSelector(
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    buttonColors: ButtonColors = ButtonDefaults.buttonColors(),
    dialogTitle: String = "Select Ticket Type",
    ticketTypes: List<String> = listOf("VIP", "VVIP", "Regular", "Early Bird", "Special")
) {
    // State for managing the dialog visibility
    var showDialog by remember { mutableStateOf(false) }

    // Handle dialog visibility and type selection
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = dialogTitle,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    ticketTypes.forEach { type ->
                        Button(
                            onClick = {
                                onTypeSelected(type)
                                showDialog = false
                            },
                            colors = buttonColors,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(type, textAlign = TextAlign.Center)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Button(
        onClick = { showDialog = true },
        colors = buttonColors
    ) {
        Text("Select Ticket Type: $selectedType")
    }
}
