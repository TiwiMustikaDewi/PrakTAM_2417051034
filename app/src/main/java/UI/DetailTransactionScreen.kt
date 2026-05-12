package com.example.praktam_2417051034.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import coil.compose.AsyncImage
import com.example.praktam_2417051034.data.model.Expense
import com.example.praktam_2417051034.ui.theme.ExpenseRed

@Composable
fun DetailTransactionScreen(
    expense: Expense?,
    onBack: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }

    if (expense == null) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        TextButton(onClick = onBack) {
            Text("← Back")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {

                AsyncImage(
                    model = expense.imageUrl,
                    contentDescription = expense.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,

                    onSuccess = { isLoading = false },
                    onError = { isLoading = false }
                )

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(expense.title, style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Rp ${expense.amount}",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Category: ${expense.category}",
            color = if (expense.category == "Needs")
                MaterialTheme.colorScheme.primary
            else
                ExpenseRed
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text("Subcategory: ${expense.subCategory}")

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Date: ${expense.date}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}