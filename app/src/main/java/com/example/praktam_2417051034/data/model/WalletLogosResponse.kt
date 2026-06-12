package com.example.praktam_2417051034.data.model

import com.google.gson.annotations.SerializedName

data class WalletLogosResponse(
    @SerializedName("walletLogos")
    val walletLogos: Map<String, String> = emptyMap()
)