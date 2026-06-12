package com.example.praktam_2417051034.data.model

import java.util.UUID

data class FinancialGoal(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val targetAmount: Long,
    var currentAmount: Long = 0L,
    val deadline: String = "",
    val emoji: String = "🎯",
    val isFavorite: Boolean = false
)