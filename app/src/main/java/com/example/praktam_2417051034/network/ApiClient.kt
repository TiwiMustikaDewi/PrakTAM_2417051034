package com.example.praktam_2417051034.network

import com.example.praktam_2417051034.model.ExpenseResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("https://gist.githubusercontent.com/TiwiMustikaDewi/b9b5d9835d28c4dfcd1012ec6df9eccf/raw/728ebdfd14d20891206ba3a0be0b4e45f1e694b5/expenses.json")
    suspend fun getExpenseData(): ExpenseResponse
}

object ApiClient {
    private const val BASE_URL = "https://gist.githubusercontent.com/TiwiMustikaDewi/b9b5d9835d28c4dfcd1012ec6df9eccf/raw/728ebdfd14d20891206ba3a0be0b4e45f1e694b5/expenses.json/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}