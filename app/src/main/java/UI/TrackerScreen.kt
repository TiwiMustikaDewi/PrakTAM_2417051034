package com.example.praktam_2417051034.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.praktam_2417051034.R
import com.example.praktam_2417051034.data.model.Expense
import com.example.praktam_2417051034.ui.theme.ExpenseRed

@Composable
fun TrackerScreen(
    expenses: List<Expense>,
    isError: Boolean,
    onRetry: () -> Unit,
    onItemClick: (Expense) -> Unit
) {
    var selectedTime by remember { mutableStateOf("All") }
    val currentTime = System.currentTimeMillis()

    val finalData = when (selectedTime) {
        "Last Week" -> expenses.filter {
            currentTime - it.date <= 7L * 24 * 60 * 60 * 1000
        }
        "Last Month" -> expenses.filter {
            currentTime - it.date <= 30L * 24 * 60 * 60 * 1000
        }
        else -> expenses
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isError && expenses.isEmpty()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Gagal memuat data", color = MaterialTheme.colorScheme.error)
                Text("Pastikan koneksi internet aktif", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRetry) { // Bagian ini tadi terpotong
                    Text("Coba Lagi")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("Expense Tracker", style = MaterialTheme.typography.titleLarge)
                }

                item {
                    Text("Transactions", style = MaterialTheme.typography.titleMedium)
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
                    ExpenseCard(
                        expense = expense,
                        onClick = { onItemClick(expense) }
                    )
                }
            }
        }
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
fun ExpenseCard(
    expense: Expense,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = expense.iconUrl,
                contentDescription = expense.title,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.food),
                error = painterResource(R.drawable.food)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
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

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Rp ${expense.amount}",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}