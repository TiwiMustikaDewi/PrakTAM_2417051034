package com.example.praktam_2417051034.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val AppTypography = Typography(

    // Judul utama (misalnya: "Expense Tracker")
    titleLarge = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold
    ),

    // Judul item (nama transaksi)
    titleMedium = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold
    ),

    // Isi (nominal, deskripsi)
    bodyMedium = TextStyle(
        fontSize = 14.sp
    ),

    // Teks kecil (tanggal, note)
    bodySmall = TextStyle(
        fontSize = 12.sp
    )
)