package com.smile.sniffer.aiFeature


import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.smile.sniffer.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL

@Composable
fun GenerateImagesScreen(navController: NavController) {
    val context = LocalContext.current
    var prompt by remember { mutableStateOf(TextFieldValue("")) }
    var imageUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Oval Ad pop-up visibility state
    var showAdPopup by remember { mutableStateOf(true) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAA2A2))
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "EXPERIENCE THE MAGIC",
                fontSize = 32.sp,
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF00796B)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Only one input field for the prompt
            CustomTextField(value = prompt, label = "Enter your prompt here") { prompt = it }

            Spacer(modifier = Modifier.height(16.dp))

            // Generate images button
            Button(
                onClick = {
                    isLoading = true
                    coroutineScope.launch {
                        val generator = ImageGenerator(context)
                        try {
                            // Generate one image with prompt and negative prompt parameters
                            generator.generate(
                                prompt = prompt.text,
                                negativePrompt = "",
                                width = 512,
                                height = 512,
                                count = 1 // Generate one image
                            ) { generatedUrls ->
                                imageUrls = generatedUrls
                            }
                        } catch (e: Exception) {
                            // Handle any exceptions that occur during the image generation
                            Toast.makeText(context, "Error generating images: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            isLoading = false // Ensure loading state is reset after processing
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Generate", color = Color.White)
            }




            Spacer(modifier = Modifier.height(16.dp))

            // Display loading indicator
            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }

            LazyColumn {
                items(imageUrls) { imageUrl ->
                    ImageItem(imageUrl = imageUrl)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Oval Ad pop-up
            if (showAdPopup) {
                OvalAdPopup(onDismiss = { showAdPopup = false })
            }
        }
    }
}


@Composable
fun CustomTextField(value: TextFieldValue, label: String, onValueChange: (TextFieldValue) -> Unit) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        decorationBox = { innerTextField ->
            Box(modifier = Modifier.padding(8.dp)) {
                if (value.text.isEmpty()) {
                    Text(label, color = Color.Gray) // Placeholder color
                }
                innerTextField()
            }
        }
    )
}


@Composable
fun OvalAdPopup(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .background(Color.White, shape = MaterialTheme.shapes.large)
            .size(400.dp, 150.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Drawable icon on the left
            Icon(
                painter = painterResource(id = R.drawable.notify),
                contentDescription = "Warning Icon",
                modifier = Modifier
                    .size(48.dp)
                    .padding(start = 16.dp),
                tint = Color.Unspecified
            )

            // Text on the right
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = "AI Generated Images May Not Be Perfect. Be descriptive and creative with your prompts.",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black
                )
            }

            // Close button on the far right
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.Black
                )
            }
        }
    }
}



@Composable
fun ImageItem(imageUrl: String) {
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .heightIn(min = 300.dp, max = 500.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )

        // Action Buttons
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            // Download Button
            IconButton(onClick = {
                isLoading = true
                // Start the download process
                downloadImage(imageUrl, context) {
                    isLoading = false // Reset loading state after download
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Share Button
            IconButton(onClick = {
                shareImage(imageUrl, context)
            }) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = Color.White
                )
            }
        }

        // Loading Indicator
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(40.dp),
                color = Color.White
            )
        }
    }
}

private fun downloadImage(imageUrl: String, context: Context, onComplete: () -> Unit) {
    // Use Coroutine to handle background work
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // Download image to local storage (Gallery)
            val response = URL(imageUrl).openStream()
            val bitmap = BitmapFactory.decodeStream(response)

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "DownloadedImage_${System.currentTimeMillis()}.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES) // For Android Q and above
            }

            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            if (uri != null) {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
            }
            onComplete()
        } catch (e: Exception) {
            e.printStackTrace()
            onComplete()
        }
    }
}


private fun shareImage(imageUrl: String, context: Context) {
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "Check out this image!")
        putExtra(Intent.EXTRA_STREAM, Uri.parse(imageUrl))
        type = "image/png"
    }
    context.startActivity(Intent.createChooser(shareIntent, null))
}
