package com.example.praktam_2417051034.data.repository

import com.example.praktam_2417051034.data.api.RetrofitClient
import com.example.praktam_2417051034.data.model.ExpenseResponse

class ExpenseRepository {
    suspend fun getExpenseData(): ExpenseResponse? {
        return try {
            RetrofitClient.instance.getExpenseData()
        } catch (e: Exception) {
            null
        }
    }
}