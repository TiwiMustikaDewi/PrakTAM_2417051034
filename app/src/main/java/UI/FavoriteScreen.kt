package com.example.praktam_2417051034.ui

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.praktam_2417051034.model.Expense
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.background

@Composable
fun FavoriteScreen(favoriteList: List<Expense>){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Favorite Transactions",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (favoriteList.isEmpty()){
            Text(
                text = "No favorite transactions",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
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
