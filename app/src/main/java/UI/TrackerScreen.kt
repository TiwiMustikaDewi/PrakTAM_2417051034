package com.example.praktam_2417051034.ui

import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.clip
import com.example.praktam_2417051034.model.*
import com.example.praktam_2417051034.ui.theme.ExpenseRed

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
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Text(
                text = "Expense Tracker",
                style = MaterialTheme.typography.titleLarge
            )
        }
        item {
            Text(
                text = "Transactions",
                style = MaterialTheme.typography.titleMedium
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
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            else
                ExpenseRed.copy(alpha = 0.1f)
        )
    ) {
        Text(
            text = category,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyMedium
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
                MaterialTheme.colorScheme.surface
        )
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(12.dp),
            color = if (isSelected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ExpenseCard(expense: Expense){
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
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
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = expense.subCategory,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Text(
                    text = expense.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (expense.category == "Needs")
                        MaterialTheme.colorScheme.primary
                    else
                        ExpenseRed
                )
            }

            Column(
                horizontalAlignment = Alignment.End

            ){
                Text(
                    text = "Rp ${expense.amount}",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
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