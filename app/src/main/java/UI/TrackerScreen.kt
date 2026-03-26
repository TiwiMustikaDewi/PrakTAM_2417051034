package com.example.praktam_2417051034.ui

import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.clip
import com.example.praktam_2417051034.model.*

@Composable
fun TrackerScreen(){

    val data = ExpenseData.expenseList

    var selectedTime by remember { mutableStateOf("All") }

    val currentTime = System.currentTimeMillis()

    val finalData = when (selectedTime) {
        "Last Week" -> data.filter {
            currentTime - it.date <= 7L * 24 * 60 * 60 * 1000
        }
        "Last Month" -> data.filter {
            currentTime - it.date <= 30L * 24 * 60 * 60 * 1000
        }
        else -> data
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Text(
                text = "Expense Tracker",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            Text(
                text = "Transactions",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(listOf("All", "Last Week", "Last Month")) { time ->
                    TimeFilterChip(
                        label = time,
                        isSelected = time == selectedTime
                    ) {
                        selectedTime = time
                    }
                }
            }
        }

        items(finalData) { expense ->
            ExpenseCard(expense)
        }
    }
}

@Composable
fun CategoryLabel(category: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (category == "Needs")
                Color(0xFFE8F5E9)
            else
                Color(0xFFFFF3E0)
        )
    ) {
        Text(
            text = category,
            modifier = Modifier.padding(12.dp),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TimeFilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                Color.LightGray
        )
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(12.dp),
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

@Composable
fun ExpenseCard(expense: Expense){
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ){
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ){

            Image(
                painter = painterResource(id = expense.imageRes),
                contentDescription = expense.title,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ){
                Text(
                    text = expense.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = expense.subCategory,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Text(
                    text = expense.category,
                    fontSize = 12.sp,
                    color = if (expense.category == "Needs")
                        Color(0xFF4CAF50)
                    else
                        Color(0xFFFF9800)
                )
            }

            Column(
                horizontalAlignment = Alignment.End

            ){
                Text(
                    text = "Rp ${expense.amount}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = {showDialog = true}) {
                    Text("See Details")
                }
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },

            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Close")
                }
            },

            title = {
                Text("Expense Details")
            },

            text = {
                Column {
                    Text("Title: ${expense.title}")
                    Text("Category: ${expense.category}")
                    Text("Subcategory: ${expense.subCategory}")
                    Text("Amount: Rp ${expense.amount}")
                }
            }
        )
    }
}