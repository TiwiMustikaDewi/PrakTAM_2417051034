package com.example.praktam_2417051034.data.api

import com.example.praktam_2417051034.data.model.WalletLogosResponse
import retrofit2.http.GET

interface ApiService {
    @GET("expenses.json")
    suspend fun getWalletLogos(): WalletLogosResponse
}