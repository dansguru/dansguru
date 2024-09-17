package com.smile.sniffer.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.smile.sniffer.model.Ticket
import com.smile.sniffer.viewmodel.TicketViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TicketsScreen(ticketViewModel: TicketViewModel) {
    val tickets by ticketViewModel.ticketList.collectAsState()
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Use a dark background for a futuristic look
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0909)) // Dark background color
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "All Tickets",
                style = MaterialTheme.typography.headlineMedium.copy(color = Color.White),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn {
                items(tickets.filter { it.isValid }) { ticket ->
                    TicketItem(ticket = ticket, onDeleteClick = { ticketId ->
                        ticketViewModel.deleteTicket(ticketId) { result ->
                            toastMessage = if (result.isSuccess) {
                                "Ticket deleted successfully."
                            } else {
                                "Error deleting ticket: ${result.exceptionOrNull()?.message}"
                            }
                            showToast = true
                        }
                    })
                }
            }
        }

        // Show toast message
        if (showToast) {
            // Use LaunchedEffect to delay the hiding of toast
            LaunchedEffect(toastMessage) {
                Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
                delay(2000) // Show for 2 seconds
                showToast = false
            }
        }
    }

    // Check for expired tickets
    LaunchedEffect(Unit) {
        while (true) {
            val now = System.currentTimeMillis()
            tickets.forEach { ticket ->
                // Parse the expiration date
                val expirationDate = try {
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(ticket.expirationDate)
                } catch (e: Exception) {
                    null
                }

                // Check if ticket has expired
                if (expirationDate != null && expirationDate.before(Date(now))) {
                    ticketViewModel.deleteTicket(ticket.id!!) { result ->
                        toastMessage = if (result.isSuccess) {
                            "Expired ticket deleted successfully."
                        } else {
                            "Error deleting expired ticket: ${result.exceptionOrNull()?.message}"
                        }
                        showToast = true
                    }
                }
            }
            delay(60000) // Check every minute
        }
    }
}




@Composable
fun TicketItem(ticket: Ticket, onDeleteClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth()
            .background(Color(0xFF0F0F0F)), // Slightly lighter dark background for the card
        elevation = CardDefaults.cardElevation(8.dp),
        shape = MaterialTheme.shapes.medium // Rounded corners
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = ticket.title,
                style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = ticket.description,
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Button(
                onClick = { ticket.id?.let { onDeleteClick(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00BCD4), // Gradient color for button
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.large // Rounded corners
            ) {
                Text("Delete Ticket", style = MaterialTheme.typography.bodySmall.copy(color = Color.Black))
            }
        }
    }
}
