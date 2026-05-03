package com.example.praktam_2417051034.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.praktam_2417051034.R
import com.example.praktam_2417051034.model.Expense
import com.example.praktam_2417051034.model.ServiceItem
import com.example.praktam_2417051034.model.ServiceMenu
import com.example.praktam_2417051034.ui.theme.ExpenseRed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    services: List<ServiceItem>,
    expenseList: List<Expense>,
    favoriteList: List<Expense>,
    onToggleFavorite: (Expense) -> Unit,
    onNavigateToFavorite: () -> Unit,
    onItemClick: (Expense) -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var isSyncing by remember { mutableStateOf(false) }

    val totalNeeds = expenseList
        .filter { it.category == "Needs" }
        .sumOf { it.amount }

    val totalWants = expenseList
        .filter { it.category == "Wants" }
        .sumOf { it.amount }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Hello", style = MaterialTheme.typography.bodySmall)
                    Text(text = "Tiwi Mustika Dewi", style = MaterialTheme.typography.titleLarge)
                }

                Button(
                    onClick = {
                        scope.launch {
                            isSyncing = true
                            delay(2000)
                            isSyncing = false
                            snackbarHostState.showSnackbar("Data synced successfully!")
                        }
                    },
                    enabled = !isSyncing
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Sync")
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                item { BalanceCard("Current Balance", 987123, "+ 123.567") }
                item { BalanceCard("Cash", 350000, "Wallet") }
                item { BalanceCard("Bank", 637123, "BCA / Card") }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Services", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                services.forEach { service ->
                    ServiceItem(iconUrl = service.iconUrl, label = service.label)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Total Needs", fontWeight = FontWeight.Bold)
                        Text("Rp $totalNeeds", color = MaterialTheme.colorScheme.primary)
                    }
                    Column {
                        Text("Total Wants", fontWeight = FontWeight.Bold)
                        Text("Rp $totalWants", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "My Favorite", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = ">",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onNavigateToFavorite() }
                )
            }

            Text(text = "Recent Transaction", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

            expenseList.forEach { expense ->
                ExpenseItem(
                    expense = expense,
                    isFavorite = favoriteList.any { it.title == expense.title },
                    onToggleFavorite = { onToggleFavorite(expense) },
                    onClick = { onItemClick(expense) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ExpenseItem(
    expense: Expense, 
    isFavorite: Boolean, 
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    val backgroundColor = if (expense.category == "Needs")
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    else
        ExpenseRed.copy(alpha = 0.1f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = expense.iconUrl,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                contentScale = ContentScale.Fit,
                placeholder = painterResource(R.drawable.ic_launcher_background),
                error = painterResource(R.drawable.ic_launcher_background)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(expense.title, fontWeight = FontWeight.Bold)
                Text(expense.category)
            }

            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = { onToggleFavorite() }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) ExpenseRed else MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(text = "Rp ${expense.amount}", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ServiceItem(iconUrl: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = iconUrl,
            contentDescription = label,
            modifier = Modifier.size(40.dp),
            placeholder = painterResource(R.drawable.notes),
            error = painterResource(R.drawable.notes)
        )
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun BalanceCard(title: String, amount: Int, subtitle: String) {
    Card(
        modifier = Modifier.width(260.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Rp $amount", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = subtitle, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
        }
    }
}
