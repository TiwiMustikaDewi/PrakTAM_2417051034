package com.example.praktam_2417051034.data.repository

import com.example.praktam_2417051034.data.api.RetrofitClient
import com.example.praktam_2417051034.data.model.WalletLogosResponse

class ExpenseRepository {
    suspend fun getWalletLogos(): WalletLogosResponse? {
        return try {
            RetrofitClient.instance.getWalletLogos()
        } catch (e: Exception) {
            null
        }
    }
}