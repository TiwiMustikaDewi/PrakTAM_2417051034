package com.example.praktam_2417051034.ui

import com.example.praktam_2417051034.model.Expense
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.example.praktam_2417051034.model.ServiceItem

@Composable
fun MainScreen(
    expenses: List<Expense>,
    services: List<ServiceItem>,
    isLoading: Boolean = false, // Tambahkan parameter ini dari MainActivity
    isError: Boolean = false,   // Tambahkan parameter ini dari MainActivity
    onRetry: () -> Unit = {}    // Tambahkan parameter ini dari MainActivity
) {
    var currentScreen by remember { mutableStateOf("dashboard") }
    var favoriteList by remember { mutableStateOf(listOf<Expense>()) }
    var selectedExpenseDetail by remember { mutableStateOf<Expense?>(null) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentScreen == "dashboard",
                    onClick = { currentScreen = "dashboard" },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = currentScreen == "tracker",
                    onClick = { currentScreen = "tracker" },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Tracker") },
                    label = { Text("Tracker") }
                )
                NavigationBarItem(
                    selected = currentScreen == "notification",
                    onClick = { currentScreen = "notification" },
                    icon = { Icon(Icons.Default.Notifications, contentDescription = "Notification") },
                    label = { Text("Alerts") }
                )
                NavigationBarItem(
                    selected = currentScreen == "notes",
                    onClick = { currentScreen = "notes" },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Notes") },
                    label = { Text("Notes") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                when (currentScreen) {
                    "dashboard" -> DashboardScreen(
                        services = services,
                        expenseList = expenses,
                        favoriteList = favoriteList,
                        onToggleFavorite = { expense ->
                            if (favoriteList.any { it.title == expense.title }) {
                                favoriteList = favoriteList.filter { it.title != expense.title }
                            } else {
                                favoriteList = favoriteList + expense
                            }
                        },
                        onNavigateToFavorite = { currentScreen = "favorite" },
                        onItemClick = { expense ->
                            selectedExpenseDetail = expense
                            currentScreen = "detail"
                        }
                    )
                    "tracker" -> TrackerScreen(
                        expenses = expenses,
                        isError = isError,
                        onRetry = onRetry,
                        onItemClick = { expense ->
                            selectedExpenseDetail = expense
                            currentScreen = "detail"
                        }
                    )
                    "notification" -> NotificationScreen()
                    "favorite" -> FavoriteScreen(
                        favoriteList = favoriteList,
                        onItemClick = { expense ->
                            selectedExpenseDetail = expense
                            currentScreen = "detail"
                        }
                    )
                    "notes" -> NotesScreen()
                    "detail" -> DetailTransactionScreen(
                        expense = selectedExpenseDetail,
                        onBack = { currentScreen = "dashboard" }
                    )
                }
            }
        }
    }
}