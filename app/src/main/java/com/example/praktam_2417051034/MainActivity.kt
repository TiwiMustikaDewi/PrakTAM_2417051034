package com.example.praktam_2417051034

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.praktam_2417051034.ui.MainScreen
import com.example.praktam_2417051034.ui.theme.PrakTAM_2417051034Theme
import com.example.praktam_2417051034.viewmodel.MainViewModel
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PrakTAM_2417051034Theme {

                val viewModel: MainViewModel = viewModel()
                MainScreen(
                    expenses = viewModel.expenseList,
                    services = viewModel.serviceList,
                    isLoading = viewModel.isLoading,
                    isError = viewModel.isError,
                    onRetry = { viewModel.fetchData() }
                )
            }
        }
    }
}
