package com.example.praktam_2417051034.ui

import androidx.compose.runtime.Composable
import  androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier


@Composable
fun TrackerScreen(){
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ){
        Text(
            text = "Expense Tracker",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Track and review your expense here")
    }
}