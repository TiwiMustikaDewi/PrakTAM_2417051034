package com.example.praktam_2417051034

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.praktam_2417051034.data.repository.ExpenseRepository
import com.example.praktam_2417051034.data.model.Expense
import com.example.praktam_2417051034.data.model.ServiceItem
import com.example.praktam_2417051034.ui.MainScreen
import com.example.praktam_2417051034.ui.theme.PrakTAM_2417051034Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val repository = remember { ExpenseRepository() }

            var expenseList by remember { mutableStateOf<List<Expense>>(emptyList()) }
            var serviceList by remember { mutableStateOf<List<ServiceItem>>(emptyList()) }
            var isLoading by remember { mutableStateOf(true) }
            var isError by remember { mutableStateOf(false) }

            val fetchData: () -> Unit = {
                lifecycleScope.launch {
                    isLoading = true
                    isError = false
                    try {
                        val response = repository.getExpenseData()

                        if (response != null) {
                            expenseList = response.expenses
                            serviceList = response.services

                            isError = expenseList.isEmpty()
                            Log.d("API_SUCCESS", "Data berhasil dimuat: ${expenseList.size} item")
                        } else {
                            isError = true
                        }
                    } catch (e: Exception) {
                        isError = true
                        Log.e("API_ERROR", "Gagal fetch data: ${e.message}")
                    } finally {
                        isLoading = false
                    }
                }
            }
            LaunchedEffect(Unit) {
                fetchData()
            }

            PrakTAM_2417051034Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (isError) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Gagal Memuat Data",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Red
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Pastikan koneksi internet Anda menyala",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { fetchData() }) {
                                    Text("Coba Lagi")
                                }
                            }
                        }
                    } else {
                        MainScreen(
                            expenses = expenseList,
                            services = serviceList
                        )
                    }
                }
            }
        }
    }
}