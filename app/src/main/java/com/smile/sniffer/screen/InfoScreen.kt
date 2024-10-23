package com.smile.sniffer.screen


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.Icon
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Whatsapp
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.smile.sniffer.R

@Composable
fun InfoScreen(navController: NavController) {
    var agreed by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.split),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().blur(16.dp)
        )

        if (!agreed) {
            // Terms and Services Agreement Screen
            TermsAgreement { agreed = true }
        } else {
            // Main Content after Agreement
            MainContent()
        }
    }
}

@Composable
fun TermsAgreement(onAgree: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "By reading the following content, you agree with the terms and services of the app. " +
                    "The app has experimental features that may delay but soon comply. Are you ready?",
            color = Color.White,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onAgree,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Text(text = "I Agree, Proceed")
        }
    }
}

@Composable
fun MainContent() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ExpandableInfoCard(
            title = "Did You Know?",
            content = "The screen you will navigate to will allow you to access your real-time location. " +
                    "Isn't that great? You can find ongoing events and real-time locations of these events in your area!"
        )
        ExpandableInfoCard(
            title = "Poster Upload Fun!",
            content = "In the illustration screen, you will be required to upload your favorite poster with your design. " +
                    "Imagine how amazing your poster will attract more sales!"
        )
        ExpandableInfoCard(
            title = "Get Ready for the Magic!",
            content = "Brace yourself for the fun of creating your own poster! Imagine designing something so epic that your fans can't help but say, 'Wow!' " +
                    "With just a few clicks, you'll turn your ideas into visual masterpieces."
        )
        ExpandableInfoCard(
            title = "AI Magic Just for You!",
            content = "Buckle up, because things are about to get futuristic! Our AI will generate your poster AND tickets with a single click."
        )
        ContactCard(
            contactInfo = "Got Questions? Need help? Don't panic! We're just a message away. " +
                    "Click the icons below to initiate communication\uD83D\uDEE1\uFE0F.",
            contactNumber = "+254745342479"
        )
    }
}



@Composable
fun ExpandableInfoCard(title: String, content: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF000000).copy(alpha = 0.7f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (expanded) {
                Text(
                    text = content,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (expanded) "Show Less" else "Show More",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.clickable { expanded = !expanded }
            )
        }
    }
}

@Composable
fun ContactCard(contactInfo: String, contactNumber: String) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0C0133).copy(alpha = 0.7f),
            contentColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = contactInfo,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.Whatsapp,
                    contentDescription = "WhatsApp",
                    tint = Color.White,
                    modifier = Modifier
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$contactNumber"))
                            context.startActivity(intent)
                        }
                        .size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    imageVector = Icons.Filled.Phone,
                    contentDescription = "Call",
                    tint = Color.White,
                    modifier = Modifier
                        .clickable {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$contactNumber"))
                            context.startActivity(intent)
                        }
                        .size(24.dp)
                )
            }
        }
    }
}