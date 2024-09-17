package com.smile.sniffer.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.smile.sniffer.viewmodel.TicketViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@Composable
fun IllustratedTicketScreen(
    navController: NavController,
    viewModel: TicketViewModel = androidx.lifecycle.viewmodel.compose.viewModel() // Correctly obtain the viewModel
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var expiryDate by remember { mutableStateOf<LocalDate?>(null) }
    var expiryTime by remember { mutableStateOf<LocalTime?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Image Picker
        Button(onClick = {
            imagePickerLauncher.launch("image/*")
        }) {
            Text("Select Image")
        }

        Spacer(modifier = Modifier.height(8.dp))

        selectedImageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Expiry Date Picker
        OutlinedButton(onClick = {
            showDatePicker(context) { date ->
                expiryDate = date
            }
        }) {
            Text("Select Expiry Date")
        }

        Spacer(modifier = Modifier.height(8.dp))

        expiryDate?.let {
            Text("Selected Date: $it") // Removed redundant curly braces
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Expiry Time Picker
        OutlinedButton(onClick = {
            showTimePicker(context) { time ->
                expiryTime = time
            }
        }) {
            Text("Select Expiry Time")
        }

        Spacer(modifier = Modifier.height(8.dp))

        expiryTime?.let {
            Text("Selected Time: $it") // Removed redundant curly braces
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Upload Button
        Button(
            onClick = {
                isUploading = true
                viewModel.uploadPicture(selectedImageUri, expiryDate, expiryTime) { success, error ->
                    isUploading = false
                    if (success) {
                        showToast(context, "Posted successfully", Toast.LENGTH_SHORT)
                    } else {
                        uploadError = error
                    }
                }
            },
            enabled = !isUploading
        ) {
            Text("Upload Picture")
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isUploading) {
            CircularProgressIndicator()
        }

        uploadError?.let {
            Text(
                text = "Error: $it",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

fun showDatePicker(context: Context, onDateSelected: (LocalDate) -> Unit) {
    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePicker.show()
}

fun showTimePicker(context: Context, onTimeSelected: (LocalTime) -> Unit) {
    val calendar = Calendar.getInstance()
    val timePicker = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            onTimeSelected(LocalTime.of(hourOfDay, minute))
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )
    timePicker.show()
}

fun showToast(context: Context, message: String, duration: Int) {
    Toast.makeText(context, message, duration).show()
}

@Preview
@Composable
fun PreviewIllustratedTicketScreen() {
    IllustratedTicketScreen(navController = NavController(LocalContext.current))
}
