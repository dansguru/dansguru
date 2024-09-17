package com.smile.sniffer.screen

import android.app.TimePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.smile.sniffer.viewmodel.TicketViewModel
import com.smile.sniffer.modules.TicketTypeSelector
import androidx.compose.ui.res.painterResource
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import androidx.navigation.NavController
import com.smile.sniffer.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualEntryScreen(navController: NavController, viewModel: TicketViewModel) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var prize by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf<LocalDate?>(null) }
    var eventTime by remember { mutableStateOf<LocalTime?>(null) }
    var place by remember { mutableStateOf("") }
    var attireStyle by remember { mutableStateOf("") }
    var openTime by remember { mutableStateOf<LocalTime?>(null) }
    var closeTime by remember { mutableStateOf<LocalTime?>(null) }
    var ticketType by remember { mutableStateOf("Regular") }
    var showError by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    var expirationDate by remember { mutableStateOf<LocalDate?>(null) }

    // Control state for showing dialogs
    var showDatePicker by remember { mutableStateOf(false) }
    var showEventTimePicker by remember { mutableStateOf(false) }
    var showOpenTimePicker by remember { mutableStateOf(false) }
    var showCloseTimePicker by remember { mutableStateOf(false) }
    var showExpirationDatePicker by remember { mutableStateOf(false) }

    // Countdown timer state
    var countdown by remember { mutableStateOf("") }

    // Function to calculate countdown
    fun updateCountdown() {
        val now = LocalDate.now().atTime(LocalTime.now())
        val eventDateTime = eventDate?.atTime(eventTime ?: LocalTime.now())
        if (eventDateTime != null) {
            val duration = java.time.Duration.between(now, eventDateTime)
            countdown = if (duration.isNegative) {
                "Ticket Expired"
            } else {
                val hours = duration.toHours()
                val minutes = duration.toMinutes() % 60
                val seconds = duration.seconds % 60
                String.format("%02d:%02d:%02d", hours, minutes, seconds)
            }
        } else {
            countdown = "No event date and time set"
        }
    }

    // Update countdown every second
    LaunchedEffect(eventDate, eventTime) {
        while (true) {
            updateCountdown()
            delay(1000L) // Update every second
        }
    }

    val backgroundImage: Painter = painterResource(id = R.drawable.party)

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black) // Optional: This ensures a background color if the image is not loaded
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = "Background Image",
            modifier = Modifier
                .fillMaxSize()
                .blur(2.dp) // Apply blur effect here
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.1f))
        ) {
            // TopAppBar with Back Button
            TopAppBar(
                title = { Text("Make your ticket here") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Filled.KeyboardDoubleArrowLeft, contentDescription = "Back")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Create a New Ticket",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Ticket Input Fields
            TicketInputFields(
                title = title, onTitleChange = { title = it },
                description = description, onDescriptionChange = { description = it },
                prize = prize, onPrizeChange = { prize = it },
                place = place, onPlaceChange = { place = it },
                attireStyle = attireStyle, onAttireStyleChange = { attireStyle = it },
            )

            // Date Picker for Event Date
            OutlinedButton(onClick = { showDatePicker = true }) {
                Text(text = eventDate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: "Select Event Date")
            }

            // Time Picker for Event Time
            OutlinedButton(onClick = { showEventTimePicker = true }) {
                Text(text = eventTime?.format(DateTimeFormatter.ISO_LOCAL_TIME) ?: "Select Event Time")
            }

            // Time Picker for Open Time
            OutlinedButton(onClick = { showOpenTimePicker = true }) {
                Text(text = openTime?.format(DateTimeFormatter.ISO_LOCAL_TIME) ?: "Select Open Time")
            }

            // Date Picker for Expiration Date
            OutlinedButton(onClick = { showExpirationDatePicker = true }) {
                Text(text = expirationDate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: "Select Expiration Date")
            }

            // Time Picker for Close Time
            OutlinedButton(onClick = { showCloseTimePicker = true }) {
                Text(text = closeTime?.format(DateTimeFormatter.ISO_LOCAL_TIME) ?: "Select Close Time")
            }

            // Ticket Type Selector
            TicketTypeSelector(selectedType = ticketType, onTypeSelected = { type -> ticketType = type })

            // Countdown Timer
            Text(
                text = "Time Remaining: $countdown",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Create Ticket Button
            Spacer(modifier = Modifier.height(16.dp))
            CreateTicketButton(isLoading = isLoading) {
                if (title.isNotBlank() && eventDate != null && eventTime != null) {
                    viewModel.addTicket(
                        title,
                        description,
                        prize,
                        eventDate!!.toString() + "T" + eventTime!!.toString(),
                        place,
                        attireStyle,
                        openTime?.toString() ?: "",
                        closeTime?.toString() ?: "",
                        ticketType,
                        expirationDate!!.toString()
                    ) { result ->
                        result.fold(
                            onSuccess = {
                                successMessage = "Ticket created successfully!"
                                showError = false
                            },
                            onFailure = { e ->
                                successMessage = ""
                                showError = true
                            }
                        )
                    }
                } else {
                    showError = true
                    successMessage = "Title, event date, and event time cannot be empty."
                }
            }

            // Loading Indicator
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
            }

            // Success/Error Messages
            TicketCreationMessages(successMessage = successMessage, showError = showError)
        }
    }

    // Show dialogs based on state
    if (showDatePicker) {
        ShowDatePickerDialog { selectedDate ->
            eventDate = selectedDate
            showDatePicker = false
        }
    }

    if (showEventTimePicker) {
        ShowTimePickerDialog { selectedTime ->
            eventTime = selectedTime
            showEventTimePicker = false
        }
    }

    if (showOpenTimePicker) {
        ShowTimePickerDialog { selectedTime ->
            openTime = selectedTime
            showOpenTimePicker = false
        }
    }

    if (showExpirationDatePicker) {
        ShowExpirationDatePickerDialog { selectedDate ->
            expirationDate = selectedDate
            showExpirationDatePicker = false
        }
    }

    if (showCloseTimePicker) {
        ShowTimePickerDialog { selectedTime ->
            closeTime = selectedTime
            showCloseTimePicker = false
        }
    }
}

// Functions moved out for better reusability

@Composable
fun ShowDatePickerDialog(onDateSelected: (LocalDate) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            onDateSelected(selectedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.show()
}

@Composable
fun ShowTimePickerDialog(onTimeSelected: (LocalTime) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val selectedTime = LocalTime.of(hourOfDay, minute)
            onTimeSelected(selectedTime)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )
    timePickerDialog.show()
}

// Modular Components

@Composable
fun TicketInputFields(
    title: String, onTitleChange: (String) -> Unit,
    description: String, onDescriptionChange: (String) -> Unit,
    prize: String, onPrizeChange: (String) -> Unit,
    place: String, onPlaceChange: (String) -> Unit,
    attireStyle: String, onAttireStyleChange: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = prize,
            onValueChange = onPrizeChange,
            label = { Text("Prize") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = place,
            onValueChange = onPlaceChange,
            label = { Text("Place") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = attireStyle,
            onValueChange = onAttireStyleChange,
            label = { Text("Attire Style") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
@Composable
fun CreateTicketButton(isLoading: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(text = "Create Ticket")
        }
    }
}

@Composable
fun TicketCreationMessages(successMessage: String, showError: Boolean) {
    if (showError) {
        Text(
            text = "An error occurred. Please try again.",
            color = Color.Red,
            style = MaterialTheme.typography.bodyMedium
        )
    }
    if (successMessage.isNotEmpty()) {
        Text(
            text = successMessage,
            color = Color.Green,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@Composable
fun ShowExpirationDatePickerDialog(onDateSelected: (LocalDate) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            onDateSelected(selectedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.show()
}
