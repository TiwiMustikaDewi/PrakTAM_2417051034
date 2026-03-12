package com.example.praktam_2417051034.ui
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

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
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text="Hello",
            color=Color.Gray
        )
        Text(
            text="Tiwi Mustika Dewi",
            fontSize= 20.sp,
            fontWeight=FontWeight.Bold
        )
        Spacer(modifier=Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF6C8CF5)
            )
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Current Balance",
                    color = Color.White
                )

                Text(
                    text = "Rp 987123",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        Spacer(modifier=Modifier.height(20.dp))

        Text(
            text = "Services",
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            ServiceItem(R.drawable.notes, "Notes")
            ServiceItem(R.drawable.tracker, "Tracker")
            ServiceItem(R.drawable.cashflow, "Cashflow")
            ServiceItem(R.drawable.paylater, "Paylater")

        }
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

        Text(
            text = "Recent Transaction",
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        expenseList.forEach { expense ->
            ExpenseItem(expense)
            Spacer(modifier = Modifier.height(8.dp))
        }

        }
    }


@Composable
fun ExpenseItem(expense: Expense) {
    var showDialog by remember { mutableStateOf(false) }

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
    )

    {
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

            Column(modifier = Modifier.weight(1f)) {
                Text(expense.title, fontWeight = FontWeight.Bold)
                Text(expense.category)

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.height(30.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = "See Details",
                        fontSize = 12.sp
                    )
                }
            }

            Text(
                text="Rp ${expense.amount}",
                fontWeight=FontWeight.Bold
            )
        }

    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },

            confirmButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
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
                    Text("Amount: Rp ${expense.amount}")
                }
            }
        )
    }
}
@Composable
fun ServiceItem(icon: Int, label: String) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(icon),
            contentDescription = label,
            modifier = Modifier.size(40.dp)
        )

        Text(
            text = label,
            fontSize = 12.sp
        )
    }
}

