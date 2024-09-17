package com.smile.sniffer.model

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.smile.sniffer.ui.theme.*
import androidx.compose.ui.platform.LocalDensity

sealed class Screen(val route: String, val label: String, val icon: @Composable () -> Unit) {
    data object Home : Screen("home", "Home", { Icon(Icons.Default.Home, contentDescription = "Home") })
    data object TicketChoice : Screen("ticket_choice", "Create", { Icon(Icons.Default.Create, contentDescription = "Create Ticket") })
    data object TicketVerification : Screen("ticketVerification", "Verify", { Icon(Icons.Default.Verified, contentDescription = "Verify Ticket") })
    data object TicketScreen : Screen("tickets", "Manager", { Icon(Icons.Default.History, contentDescription = "Ticket Manager") })
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val screens = listOf(
        Screen.Home,
        Screen.TicketChoice,
        Screen.TicketVerification,
        Screen.TicketScreen
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var selectedIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(currentRoute) {
        selectedIndex = screens.indexOfFirst { it.route == currentRoute }
    }

    // Animated Ball Transition
    val transition = rememberInfiniteTransition(label = "")
    val animatedBallOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 500, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ), label = ""
    )

    val density = LocalDensity.current.density // Get the density for dp to px conversion
    val iconWidthPx = with(LocalDensity.current) { 80.dp.toPx() } // Convert dp to px

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent) // Ensure transparent background
            .shadow(elevation = 8.dp) // Floating effect
    ) {
        Column {
            NavigationBar(
                modifier = Modifier
                    .background(Color.Transparent) // Ensure transparent background
                    .shadow(elevation = 8.dp) // Floating effect
            ) {
                screens.forEachIndexed { index, screen ->
                    val isSelected = selectedIndex == index

                    NavigationBarItem(
                        icon = screen.icon,
                        label = { Text(screen.label) },
                        selected = isSelected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Purple80, // Use your custom color
                            unselectedIconColor = PurpleGrey40, // Use your custom color
                            selectedTextColor = Purple80, // Use your custom color
                            unselectedTextColor = PurpleGrey40, // Use your custom color
                            indicatorColor = Purple80.copy(alpha = 0.2f) // Use your custom color
                        )
                    )
                }
            }

            // Ball Animation
            val ballOffsetX = selectedIndex * iconWidthPx

            Canvas(modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .padding(horizontal = 16.dp)
            ) {
                drawCircle(
                    color = Purple80, // Use your custom color
                    radius = 10f + animatedBallOffset * 10f,
                    center = Offset(
                        x = ballOffsetX + (iconWidthPx / 2), // Center the ball under the selected icon
                        y = size.height / 2 // Center vertically in the Canvas
                    )
                )
            }
        }
    }
}
