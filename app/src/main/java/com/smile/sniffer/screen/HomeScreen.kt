package com.smile.sniffer.screen

import android.util.Base64
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.smile.sniffer.viewmodel.TicketViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.res.stringResource
import com.smile.sniffer.R
import com.smile.sniffer.model.Ticket
import com.smile.sniffer.modules.ShimmerEffect
import com.smile.sniffer.modules.TicketItem
import com.smile.sniffer.mpesa.PhoneNumberDialog
import com.smile.sniffer.api.FirestoreApiService
import com.smile.sniffer.api.PictureData
import com.smile.sniffer.modules.PictureItem
import com.smile.sniffer.mpesa.RetrofitClient
import com.smile.sniffer.mpesa.StkPushRequest
import com.smile.sniffer.mpesa.getAccessToken
import com.smile.sniffer.viewmodel.TicketCreationState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavController,
    ticketViewModel: TicketViewModel
) {
    val tickets by ticketViewModel.ticketList.collectAsState()
    val ticketCreationState by ticketViewModel.ticketCreationState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var picturesList by remember { mutableStateOf<List<PictureData>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedTicket by remember { mutableStateOf<Ticket?>(null) }
    var isLoading by remember { mutableStateOf(true) } // To manage loading state

    LaunchedEffect(Unit) {
        ticketViewModel.loadTickets()
        coroutineScope.launch {
            // Simulate data fetching
            try {
                val fetchedPictures = FirestoreApiService().fetchPicturesData()
                picturesList = fetchedPictures
            } catch (e: Exception) {
                // Handle any errors here
            } finally {
                isLoading = false
            }
        }
    }

    if (showDialog && selectedTicket != null) {
        PhoneNumberDialog(
            visible = showDialog,
            onDismiss = { showDialog = false },
            onConfirm = { phoneNumber ->
                coroutineScope.launch {
                    selectedTicket?.let { ticket -> handleBuyClick(ticket, phoneNumber) }
                }
                showDialog = false
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AsyncImage(
            model = R.drawable.party,
            contentDescription = stringResource(id = R.string.background_image_description),
            modifier = Modifier
                .fillMaxSize()
                .blur(12.dp),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (isLoading) {
                // Display shimmer effect while loading
                repeat(3) {
                    ShimmerEffect()
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else {
                when (ticketCreationState) {
                    is TicketCreationState.Loading -> {
                        // Display shimmer effect for tickets while loading
                        repeat(3) {
                            ShimmerEffect()
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    is TicketCreationState.Error -> {
                        Text(
                            text = (ticketCreationState as TicketCreationState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Display Ticket Items
                            items(tickets) { ticket ->
                                TicketItem(
                                    ticket = ticket,
                                    isLoading = false,
                                    onClick = {},
                                    onPaymentClick = {
                                        selectedTicket = ticket
                                        showDialog = true
                                    }
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // Fetch and Display Picture Items
                            items(picturesList) { pictureData ->
                                PictureItem(
                                    imageUrl = pictureData.pictureUrl,
                                    description = pictureData.expirationDateTime,
                                    isLoading = isLoading,
                                    expirationDateTime = pictureData.expirationDateTime
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}



suspend fun handleBuyClick(ticket: Ticket, phoneNumber: String) {
    val service = RetrofitClient.instance
    val accessToken = getAccessToken() // Fetch access token dynamically

    val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
    val password = generateMpesaPassword(
        shortcode = "174379",
        passkey = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919",
        timestamp = timestamp
    )

    val authHeader = "Bearer $accessToken"


    val request = StkPushRequest(
        BusinessShortCode = "174379",
        Password = password,
        Timestamp = timestamp,
        TransactionType = "CustomerPayBillOnline",
        Amount = ticket.prize,
        PartyA = phoneNumber,
        PartyB = "0111844067",
        PhoneNumber = phoneNumber,
        CallBackURL = "http://mpesa-requestbin.herokuapp.com/14vfhf01",
        AccountReference = "TICKET_ID_${ticket.id}",
        TransactionDesc = "Payment for ticket ${ticket.title}"
    )

    try {
        val response = service.stkPushRequest(authHeader, request)
        if (response.isSuccessful) {
            val stkPushResponse = response.body()
            if (stkPushResponse?.ResponseCode == "0") {
                println("STK Push request successful: ${stkPushResponse.ResponseDescription}")
                // Proceed with updating transaction status in DB or UI
            } else {
                println("STK Push request failed: ${stkPushResponse?.ResponseDescription}")
                // Handle failed response, retry, or show user feedback
            }
        } else {
            val errorBody = response.errorBody()?.string() ?: "Unknown error"
            println("STK Push request failed: $errorBody")
            // Handle server-side error, maybe retry based on HTTP status
        }
    } catch (e: Exception) {
        e.printStackTrace()
        println("STK Push request error: ${e.message}")
        // Handle connection or timeout errors
    }
}

fun generateMpesaPassword(shortcode: String, passkey: String, timestamp: String): String {
    val input = "$shortcode$passkey$timestamp"
    return Base64.encodeToString(input.toByteArray(), Base64.NO_WRAP)
}

