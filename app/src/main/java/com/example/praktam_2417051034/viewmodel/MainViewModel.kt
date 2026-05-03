package com.example.praktam_2417051034.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.praktam_2417051034.model.Expense
import com.example.praktam_2417051034.model.ServiceItem
import kotlinx.coroutines.launch
import com.example.praktam_2417051034.network.ApiClient
import android.util.Log

class MainViewModel : ViewModel() {
    var expenseList by mutableStateOf<List<Expense>>(emptyList())
    var serviceList by mutableStateOf<List<ServiceItem>>(emptyList())

    var isError by mutableStateOf(false)
    var isLoading by mutableStateOf(true)

    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            isLoading = true
            isError = false
            try {
                val response = ApiClient.apiService.getExpenseData()
                expenseList = response.expenses
                serviceList = response.services
                Log.d("API_SUCCESS", "Data berhasil diambil: ${expenseList.size} items")
            } catch (e: Exception) {
                isError = true
                Log.e("API_ERROR", "Penyebab Gagal: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
}