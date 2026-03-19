package com.example.praktam_2417051034.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import com.example.praktam_2417051034.model.Expense


@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf("dashboard") }
    var favoriteList by remember {mutableStateOf(listOf<Expense>())}
    Scaffold(
        bottomBar = {
            NavigationBar{
                NavigationBarItem(
                    selected = currentScreen == "dashboard",
                    onClick = { currentScreen = "dashboard"},
                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard")},
                    label = { Text("Home") }
                )

                NavigationBarItem(
                    selected = currentScreen == "tracker",
                    onClick = { currentScreen = "tracker"},
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Tracker")},
                    label = { Text("Tracker") }
                )
            NavigationBarItem(
                selected = currentScreen == "notification",
                onClick = { currentScreen = "notification"},
                icon = { Icon(Icons.Default.Notifications, contentDescription = "Notification")},
                label = { Text("Alerts") }
            )
        }
}

) {
    padding -> Box(modifier = Modifier.padding(padding)){
        when (currentScreen) {
            "dashboard" -> DashboardScreen(
                favoriteList = favoriteList,
                onToggleFavorite = { expense ->
                    favoriteList =
                        if (favoriteList.contains(expense))
                            favoriteList - expense
                        else
                            favoriteList + expense
                },
                onNavigateToFavorite = {
                    currentScreen = "favorite"
                }
            )
            "tracker" -> TrackerScreen()
            "notification" -> NotificationScreen()
            "favorite" -> FavoriteScreen(favoriteList)
        }
    }
}
}

