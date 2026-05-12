package com.example.praktam_2417051034.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.praktam_2417051034.data.model.Expense
import com.example.praktam_2417051034.data.model.ServiceItem

@Composable
fun MainScreen(
    expenses: List<Expense>,
    services: List<ServiceItem>
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
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
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
                    isError = false, // Karena sudah di handle di MainActivity, set saja false
                    onRetry = {},
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