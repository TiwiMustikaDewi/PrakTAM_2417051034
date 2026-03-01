package com.example.praktam_2417051034.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.praktam_2417051034.R
import com.example.praktam_2417051034.model.Expense
import com.example.praktam_2417051034.model.ExpenseData

@Composable
fun DashboardScreen() {

    val expenseList = ExpenseData.expenseList

    val totalNeeds=expenseList
        .filter {it.category=="Needs"}
        .sumOf {it.amount}

    val totalWants=expenseList
        .filter {it.category=="Wants"}
        .sumOf {it.amount}

    Column(
        modifier=Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFEEC))
            .padding(16.dp)
    ) {
        Text(
            text="ExpenseLite",
            fontSize=28.sp,
            fontWeight=FontWeight.Bold
        )
        Text(
            text="Needs vs Wants Tracker",
            color=Color.Gray
        )
        Spacer(modifier=Modifier.height(20.dp))

        Card(
            modifier=Modifier.fillMaxWidth(),
            elevation=CardDefaults.cardElevation(6.dp)
        ) {
            Row(
                modifier=Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Needs", fontWeight=FontWeight.Bold)
                    Text("Rp $totalNeeds", color=Color(0xFF7EA21C))
                }
                Column {
                    Text("Total Wants", fontWeight=FontWeight.Bold)
                    Text("Rp $totalWants", color=Color(0xFF7A4FD1))
                }
            }
        }
        Spacer(modifier=Modifier.height(20.dp))

        LazyColumn {
            items(expenseList) { expense ->
                ExpenseItem(expense)
            }
        }
    }
}
@Composable
fun ExpenseItem(expense: Expense) {

    val backgroundColor=
        if (expense.category == "Needs")
            Color(0xFFCBD83B).copy(alpha=0.15f)
        else
            Color(0xFFA88AED).copy(alpha=0.15f)
    Card(
        modifier=Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors= CardDefaults.cardColors(containerColor = backgroundColor),
        shape= MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter=painterResource(id=expense.imageRes),
                contentDescription=null,
                modifier=Modifier.size(48.dp),
                contentScale=ContentScale.Fit
            )

            Spacer(modifier=Modifier.width(16.dp))

            Column(modifier=Modifier.weight(1f)) {
                Text(expense.title, fontWeight=FontWeight.Bold)
                Text(expense.category)
            }

            Text(
                text="Rp ${expense.amount}",
                fontWeight=FontWeight.Bold
            )
        }
    }
}