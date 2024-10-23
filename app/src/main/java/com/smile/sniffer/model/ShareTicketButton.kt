package com.smile.sniffer.model

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.smile.sniffer.R

@Composable
fun ShareTicketButton(contentToShare: String) {
    val context = LocalContext.current
    val shareIcon: Painter = painterResource(id = R.drawable.share)

    IconButton(
        onClick = {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, contentToShare)
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(intent, "Share Ticket Details"))
        },
        modifier = Modifier
            .padding(12.dp)
            .size(48.dp) // Adjust size as needed
    ) {
        Icon(
            painter = shareIcon,
            contentDescription = "Share Ticket",
            tint = Color.Unspecified,
            modifier = Modifier.size(24.dp)
        )
    }
}
