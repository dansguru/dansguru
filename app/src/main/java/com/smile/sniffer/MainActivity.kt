package com.smile.sniffer

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.smile.sniffer.navigation.NavGraph
import com.smile.sniffer.model.BottomNavigationBar
import com.smile.sniffer.ui.theme.SnifferTheme

class MainActivity : ComponentActivity() {
    private lateinit var requestNotificationPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (granted) {
                    Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            requestNotificationPermission()
        }

        setContent {
            SnifferTheme {
                // Set the surface color to transparent to see through it
                Surface(modifier = Modifier.background(Color.Transparent)) {
                    SnifferApp()
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

@Composable
fun SnifferApp() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        // Ensure the Surface background is transparent
        Box(modifier = Modifier
            .padding(innerPadding)
            .background(Color.Transparent) // Set background to transparent
        ) {
            NavGraph(navController = navController)
        }
    }
}
