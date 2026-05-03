package com.example.praktam_2417051034.model

import com.google.gson.annotations.SerializedName

data class DashboardData(
    val services: List<ServiceMenu>,
    val expenses: List<Expense>
)

data class ServiceMenu(
    val label: String,
    val iconUrl: String
)

data class Expense(
    val title: String,
    val amount: Int,
    val category: String,
    val subCategory: String,
    val date: Long,
    val iconUrl: String,
    val imageUrl: String
)

data class ExpenseResponse(
    val services: List<ServiceItem>,
    val expenses: List<Expense>
)

data class ServiceItem(
    val label: String,
    val iconUrl: String
)