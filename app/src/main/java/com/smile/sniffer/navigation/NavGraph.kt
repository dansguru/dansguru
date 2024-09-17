package com.smile.sniffer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smile.sniffer.screen.*
import com.smile.sniffer.viewmodel.TicketViewModel
import com.smile.sniffer.viewmodel.TicketViewModelFactory

@Composable
fun NavGraph(navController: NavHostController) {
    // Obtain ViewModel instances with context
    val context = LocalContext.current
    val ticketViewModel: TicketViewModel = viewModel(
        factory = TicketViewModelFactory(context)
    )

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController, ticketViewModel = ticketViewModel)
        }
        composable("ticket_choice") {
            TicketChoiceScreen(navController)
        }
        composable("illustrated_ticket") {
            IllustratedTicketScreen(navController, viewModel = ticketViewModel)
        }
        composable("manual_entry") {
            ManualEntryScreen(navController, viewModel = ticketViewModel)
        }
        composable("tickets") {
            TicketsScreen(ticketViewModel)
        }
        composable("ticketVerification") {
            TicketVerificationScreen(
                navController = navController,
                viewModel = ticketViewModel,
                onNavigateToHistory = {
                    navController.navigate("scan_history")
                }
            )
        }
        composable("scan_history") {
            ScanHistoryScreen(
                viewModel = ticketViewModel,
                onNavigateBack = {
                    navController.popBackStack() // Go back to previous screen
                }
            )
        }
    }
}
