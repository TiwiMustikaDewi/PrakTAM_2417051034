package com.example.praktam_2417051034.data.model

import com.google.gson.annotations.SerializedName

data class ExpenseResponse(
    @SerializedName("services")
    val services: List<ServiceItem>,
    @SerializedName("expenses")
    val expenses: List<Expense>
)

data class ServiceItem(
    @SerializedName("label")
    val label: String,
    @SerializedName("iconUrl")
    val iconUrl: String
)

data class Expense(
    @SerializedName("title")
    val title: String,
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("category")
    val category: String,
    @SerializedName("subCategory")
    val subCategory: String,
    @SerializedName("date")
    val date: Long,
    @SerializedName("iconUrl")
    val iconUrl: String,
    @SerializedName("imageUrl")
    val imageUrl: String
)