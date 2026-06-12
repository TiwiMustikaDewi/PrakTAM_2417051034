package com.example.praktam_2417051034.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.praktam_2417051034.viewmodel.AuthViewModel
import com.example.praktam_2417051034.ui.auth.ForgotPasswordScreen
import com.example.praktam_2417051034.ui.auth.LoginScreen
import com.example.praktam_2417051034.ui.auth.RegisterScreen
import com.example.praktam_2417051034.ui.dashboard.DashboardScreen
import com.example.praktam_2417051034.ui.goals.GoalsScreen
import com.example.praktam_2417051034.ui.onboarding.OnboardingScreen
import com.example.praktam_2417051034.ui.onboarding.SplashScreen
import com.example.praktam_2417051034.ui.profile.AboutScreen
import com.example.praktam_2417051034.ui.profile.EditProfileScreen
import com.example.praktam_2417051034.ui.profile.FeedbackScreen
import com.example.praktam_2417051034.ui.profile.HelpFaqScreen
import com.example.praktam_2417051034.ui.profile.NotificationScreen
import com.example.praktam_2417051034.ui.profile.ProfileScreen
import com.example.praktam_2417051034.ui.transactions.TransactionScreen
import com.example.praktam_2417051034.viewmodel.TransactionViewModel

@Composable
fun MainScreen() {
    val authViewModel: AuthViewModel               = viewModel()
    val transactionViewModel: TransactionViewModel = viewModel()
    val firebaseUser by authViewModel.user

    var appFlow          by remember { mutableStateOf("splash") }
    var authScreen       by remember { mutableStateOf("login") }
    var currentTab       by remember { mutableStateOf("home") }
    var profileSubScreen by remember { mutableStateOf("") }

    // Cek login status saat splash, lalu lanjut
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        appFlow = if (firebaseUser != null) "main" else "onboarding"
    }

    // Kalau status login berubah (misal logout), redirect ke auth
    LaunchedEffect(firebaseUser) {
        if (firebaseUser == null && appFlow == "main") {
            appFlow    = "auth"
            authScreen = "login"
        }
    }

    when (appFlow) {
        "splash"     -> SplashScreen()
        "onboarding" -> OnboardingScreen(onFinish = { appFlow = "auth" })
        "auth" -> {
            when (authScreen) {
                "login" -> LoginScreen(
                    onLoginSuccess             = { appFlow = "main" },
                    onNavigateToRegister       = { authScreen = "register" },
                    onNavigateToForgotPassword = { authScreen = "forgot_password" }
                )
                "register" -> RegisterScreen(
                    onRegisterSuccess = { authScreen = "login" },
                    onNavigateBack    = { authScreen = "login" }
                )
                "forgot_password" -> ForgotPasswordScreen(
                    onNavigateBack = { authScreen = "login" }
                )
            }
        }
        "main" -> {
            when (profileSubScreen) {
                "edit" -> EditProfileScreen(
                    authViewModel = authViewModel,
                    onBack        = { profileSubScreen = "" }
                )
                "notifications" -> NotificationScreen(
                    transactionViewModel = transactionViewModel,
                    onBack               = { profileSubScreen = "" }
                )
                "about"    -> AboutScreen(onBack = { profileSubScreen = "" })
                "help_faq" -> HelpFaqScreen(
                    onBack               = { profileSubScreen = "" },
                    onNavigateToFeedback = { profileSubScreen = "feedback" }
                )
                "feedback" -> FeedbackScreen(
                    onBack = { profileSubScreen = "help_faq" }
                )
                else -> {
                    Scaffold(
                        bottomBar = {
                            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                                NavigationBarItem(
                                    selected = currentTab == "home",
                                    onClick  = { currentTab = "home" },
                                    icon     = { Icon(if (currentTab == "home") Icons.Filled.Home else Icons.Outlined.Home, null) },
                                    label    = { Text("Home") }
                                )
                                NavigationBarItem(
                                    selected = currentTab == "transactions",
                                    onClick  = { currentTab = "transactions" },
                                    icon     = { Icon(Icons.Filled.List, null) },
                                    label    = { Text("Transactions") }
                                )
                                NavigationBarItem(
                                    selected = currentTab == "goals",
                                    onClick  = { currentTab = "goals" },
                                    icon     = { Icon(if (currentTab == "goals") Icons.Filled.Star else Icons.Outlined.Star, null) },
                                    label    = { Text("Goals") }
                                )
                                NavigationBarItem(
                                    selected = currentTab == "profile",
                                    onClick  = { currentTab = "profile" },
                                    icon     = { Icon(if (currentTab == "profile") Icons.Filled.Person else Icons.Outlined.Person, null) },
                                    label    = { Text("Profile") }
                                )
                            }
                        }
                    ) { padding ->
                        Box(modifier = Modifier.padding(padding)) {
                            when (currentTab) {
                                "home"         -> DashboardScreen(transactionViewModel = transactionViewModel)
                                "transactions" -> TransactionScreen(viewModel = transactionViewModel)
                                "goals"        -> GoalsScreen(transactionViewModel = transactionViewModel)
                                "profile"      -> ProfileScreen(
                                    authViewModel             = authViewModel,
                                    transactionViewModel      = transactionViewModel,
                                    onLogout                  = {
                                        authViewModel.logout()
                                        appFlow    = "auth"
                                        authScreen = "login"
                                    },
                                    onNavigateToEditProfile   = { profileSubScreen = "edit" },
                                    onNavigateToNotifications = { profileSubScreen = "notifications" },
                                    onNavigateToAbout         = { profileSubScreen = "about" },
                                    onNavigateToHelpFaq       = { profileSubScreen = "help_faq" }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}