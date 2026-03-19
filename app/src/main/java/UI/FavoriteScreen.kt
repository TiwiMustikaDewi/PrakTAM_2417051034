package com.example.praktam_2417051034.ui

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.praktam_2417051034.model.Expense

@Composable
fun FavoriteScreen(favoriteList: List<Expense>){
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "Favorite Transactions",
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (favoriteList.isEmpty()){
            Text("No favorite transactions")
        }else {
            favoriteList.forEach {
                ExpenseItem(
                    expense = it,
                    isFavorite = true,
                    onToggleFavorite = {}
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}