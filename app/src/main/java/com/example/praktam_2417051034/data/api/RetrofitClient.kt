package com.example.praktam_2417051034.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL =
        "https://gist.githubusercontent.com/TiwiMustikaDewi/77a3d108034e82dc888cbf5721c1ef28/raw/c1d0f4cf5a4aa767f82702fd128804c9385eea7a/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}