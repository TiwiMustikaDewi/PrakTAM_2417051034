package com.example.praktam_2417051034.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL =
        "https://gist.githubusercontent.com/TiwiMustikaDewi/b9b5d9835d28c4dfcd1012ec6df9eccf/raw/728ebdfd14d20891206ba3a0be0b4e45f1e694b5/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}