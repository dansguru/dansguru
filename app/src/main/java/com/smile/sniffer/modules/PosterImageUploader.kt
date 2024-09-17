package com.smile.sniffer.modules

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter

@Composable
fun PosterImageUploader(posterUri: Uri?, onImageSelected: (Uri?) -> Unit, onImageRemoved: () -> Unit) {
    // State for managing the photo picker dialog
    var showPhotoPickerDialog by remember { mutableStateOf(false) }
    // State for managing the image preview
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher to pick image from gallery
    val photoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { tempUri = it; showPhotoPickerDialog = true }
    }

    // Handle photo picker dialog visibility
    if (showPhotoPickerDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoPickerDialog = false },
            title = { Text("Upload Illustration Photo") },
            text = {
                Column {
                    tempUri?.let { uri ->
                        val painter = rememberAsyncImagePainter(uri)
                        Image(
                            painter = painter,
                            contentDescription = "Preview Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Button(onClick = {
                        tempUri?.let { onImageSelected(it) }
                        showPhotoPickerDialog = false
                    }) {
                        Text("Confirm Upload")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        tempUri = null
                        showPhotoPickerDialog = false
                    }) {
                        Text("Cancel")
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showPhotoPickerDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { photoPickerLauncher.launch("image/*") }) {
            Text("Upload Illustration Photo (Optional)")
        }

        Spacer(modifier = Modifier.height(16.dp))

        posterUri?.let { uri ->
            val painter = rememberAsyncImagePainter(uri)
            Image(
                painter = painter,
                contentDescription = "Poster Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onImageRemoved) {
                Text("Remove Image")
            }
        }
    }
}
