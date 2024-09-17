package com.smile.sniffer.mpesa

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PhoneNumberDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var isValid by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.9f),
        exit = fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.9f)
    ) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = {
                Text(
                    text = "Enter Your Phone Number",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            },
            text = {
                Column {
                    TextField(
                        value = phoneNumber,
                        onValueChange = {
                            phoneNumber = it
                            isValid = it.matches(Regex("^(\\+254|0)7[0-9]{8}$")) // DaraJa validation regex
                        },
                        label = { Text("Phone Number") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Done
                        ),
                        isError = !isValid,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            errorIndicatorColor = MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    if (!isValid) {
                        Text(
                            "Invalid phone number. Please enter a valid number in the format +2547xxxxxxxx or 07xxxxxxxx.",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (isValid) {
                            onConfirm(phoneNumber)
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
            shape = RoundedCornerShape(16.dp)
        )
    }
}

