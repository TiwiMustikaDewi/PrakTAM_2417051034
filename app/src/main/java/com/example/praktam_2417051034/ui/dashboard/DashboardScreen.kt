package com.example.praktam_2417051034.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.praktam_2417051034.R
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.example.praktam_2417051034.ui.profile.ProfilePhotoFromFirestore
import com.example.praktam_2417051034.data.model.PaymentMethod
import com.example.praktam_2417051034.viewmodel.TransactionViewModel
import java.util.Calendar

// ── Greeting helper ──────────────────────────────────────────────────────────
fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour in 5..11  -> "Good Morning"
        hour in 12..17 -> "Good Afternoon"
        hour in 18..20 -> "Good Evening"
        else           -> "Good Night"
    }
}

// ── Health Score helper (dipakai Dashboard & Profile) ────────────────────────
fun computeHealthScore(totalIncome: Long, totalExpense: Long): Int {
    if (totalIncome <= 0L) return 0
    val ratio = totalExpense.toFloat() / totalIncome.toFloat()
    return when {
        ratio <= 0.30f -> 100
        ratio <= 0.50f -> 85
        ratio <= 0.70f -> 70
        ratio <= 0.85f -> 55
        ratio <= 1.00f -> 35
        else           -> 10
    }
}

// ── Wallet display data ──────────────────────────────────────────────────────
data class WalletDisplayItem(
    val method: PaymentMethod,
    val emoji: String,
    val color: Color
)

val walletDisplayList = listOf(
    WalletDisplayItem(PaymentMethod.SEABANK,   "🌊", Color(0xFF0EA5E9)),
    WalletDisplayItem(PaymentMethod.SUPERBANK, "⚡", Color(0xFF7C3AED)),
    WalletDisplayItem(PaymentMethod.BSI,       "🏦", Color(0xFF059669)),
    WalletDisplayItem(PaymentMethod.BNI,       "🏛️", Color(0xFFD97706)),
    WalletDisplayItem(PaymentMethod.GOPAY,     "💚", Color(0xFF16A34A)),
    WalletDisplayItem(PaymentMethod.DANA,      "💙", Color(0xFF2563EB)),
    WalletDisplayItem(PaymentMethod.CASH,      "💵", Color(0xFF78716C)),
)

// ── Spending bar chart data (per category from transactions) ─────────────────
data class SpendingBar(val label: String, val fraction: Float, val color: Color)

// ── Screen ───────────────────────────────────────────────────────────────────
@Composable
fun DashboardScreen(
    transactionViewModel: TransactionViewModel
) {
    // ── Derived totals from TransactionViewModel ──────────────────────────────
    val totalIncome  = transactionViewModel.totalIncome
    val totalExpense = transactionViewModel.totalExpense

    // ── Fetch wallet logos from Gist (direct URL, no hash dependency) ──────────
    var walletLogos by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    LaunchedEffect(Unit) {
        try {
            val json = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                java.net.URL("https://gist.githubusercontent.com/TiwiMustikaDewi/b9b5d9835d28c4dfcd1012ec6df9eccf/raw/expenses.json").readText()
            }
            val parsed = com.google.gson.Gson().fromJson(
                json,
                com.example.praktam_2417051034.data.model.WalletLogosResponse::class.java
            )
            if (parsed?.walletLogos?.isNotEmpty() == true) {
                walletLogos = parsed.walletLogos
            }
        } catch (e: Exception) {
            // fallback: tetap tampil emoji
        }
    }

    // ── Health score (live, sama persis dengan ProfileScreen) ─────────────────
    val healthScore = computeHealthScore(totalIncome, totalExpense)
    val healthLabel = when {
        healthScore >= 75 -> "Excellent"
        healthScore >= 50 -> "Good"
        healthScore >= 30 -> "Fair"
        else              -> "Needs Attention"
    }

    // ── Category spending fractions ───────────────────────────────────────────
    val categoryTotals = mapOf(
        "Food"          to transactionViewModel.transactions
            .filter { it.type == com.example.praktam_2417051034.data.model.TransactionType.EXPENSE &&
                    it.expenseCategory == com.example.praktam_2417051034.data.model.ExpenseCategory.FOOD }
            .sumOf { it.amount },
        "Transport"     to transactionViewModel.transactions
            .filter { it.type == com.example.praktam_2417051034.data.model.TransactionType.EXPENSE &&
                    it.expenseCategory == com.example.praktam_2417051034.data.model.ExpenseCategory.TRANSPORT }
            .sumOf { it.amount },
        "Shopping"      to transactionViewModel.transactions
            .filter { it.type == com.example.praktam_2417051034.data.model.TransactionType.EXPENSE &&
                    it.expenseCategory == com.example.praktam_2417051034.data.model.ExpenseCategory.SHOPPING }
            .sumOf { it.amount },
        "Entertainment" to transactionViewModel.transactions
            .filter { it.type == com.example.praktam_2417051034.data.model.TransactionType.EXPENSE &&
                    it.expenseCategory == com.example.praktam_2417051034.data.model.ExpenseCategory.ENTERTAINMENT }
            .sumOf { it.amount },
        "Others"        to transactionViewModel.transactions
            .filter { it.type == com.example.praktam_2417051034.data.model.TransactionType.EXPENSE &&
                    it.expenseCategory == com.example.praktam_2417051034.data.model.ExpenseCategory.OTHERS }
            .sumOf { it.amount },
    )
    val maxCat = categoryTotals.values.maxOrNull()?.takeIf { it > 0 } ?: 1L

    val spendingBars = listOf(
        SpendingBar("Food",   (categoryTotals["Food"]!!.toFloat() / maxCat).coerceIn(0.03f, 1f), Color(0xFFF97316)),
        SpendingBar("Trans",  (categoryTotals["Transport"]!!.toFloat() / maxCat).coerceIn(0.03f, 1f), Color(0xFF0EA5E9)),
        SpendingBar("Shop",   (categoryTotals["Shopping"]!!.toFloat() / maxCat).coerceIn(0.03f, 1f), Color(0xFF7C3AED)),
        SpendingBar("Entert", (categoryTotals["Entertainment"]!!.toFloat() / maxCat).coerceIn(0.03f, 1f), Color(0xFFEC4899)),
        SpendingBar("Others", (categoryTotals["Others"]!!.toFloat() / maxCat).coerceIn(0.03f, 1f), Color(0xFF78716C)),
    )

    val headerGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFD6E8FF),
            Color(0xFFECF4FF),
            Color(0xFFF8F9FF)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FF))
            .verticalScroll(rememberScrollState())
    ) {

        // ── HEADER ─────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = headerGradient)
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Column {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ProfilePhotoFromFirestore(size = 48.dp, iconSize = 36.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        val firebaseUser = FirebaseAuth.getInstance().currentUser
                        val displayName  = firebaseUser?.displayName?.takeIf { it.isNotBlank() }
                            ?: firebaseUser?.email?.substringBefore("@")
                            ?: "there"
                        Column {
                            Text(
                                text = "${getGreeting()},",
                                fontSize = 14.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Normal
                            )
                            Text(
                                text = "$displayName 👋",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = Color(0xFF475569)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- Financial Health Score + Spending Chart (LIVE) ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2563EB)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    progress = { healthScore / 100f },
                                    modifier = Modifier.size(72.dp),
                                    color = Color.White,
                                    trackColor = Color.White.copy(alpha = 0.2f),
                                    strokeWidth = 7.dp
                                )
                                Text(
                                    text = "$healthScore",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Health Score",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 10.sp
                            )
                            Text(
                                text = healthLabel,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "This Month's Spending",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            spendingBars.forEach { bar ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 5.dp)
                                ) {
                                    Text(
                                        text = bar.label,
                                        color = Color.White.copy(alpha = 0.75f),
                                        fontSize = 9.sp,
                                        modifier = Modifier.width(34.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color.White.copy(alpha = 0.2f))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(bar.fraction)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(bar.color.copy(alpha = 0.9f))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Income & Expenses cards (live from ViewModel) ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        label    = "Income",
                        amount   = "Rp ${ "%,d".format(totalIncome).replace(',', '.') }",
                        color    = Color(0xFF16A34A),
                        isIncome = true,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        label    = "Expenses",
                        amount   = "Rp ${ "%,d".format(totalExpense).replace(',', '.') }",
                        color    = Color(0xFFDC2626),
                        isIncome = false,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // ── BODY ──────────────────────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {

            Spacer(modifier = Modifier.height(20.dp))

            // --- Wallets & Banks (balance from ViewModel) ---
            Text(
                text = "My Wallets & Banks",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                walletDisplayList.forEach { item ->
                    WalletCard(
                        item      = item,
                        balance   = transactionViewModel.walletBalance(item.method),
                        logoUrl   = walletLogos[item.method.name]
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- TIVO Coach Insights ---
            Text(
                text = "TIVO Coach Insights",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(10.dp))
            InsightCard(message = "Your food expenses increased 15% this week.")
            Spacer(modifier = Modifier.height(8.dp))
            InsightCard(message = "Save Rp200.000/month by reducing entertainment spending.")

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ── Composables ───────────────────────────────────────────────────────────────

@Composable
private fun SummaryCard(
    label: String,
    amount: String,
    color: Color,
    isIncome: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(text = amount, fontSize = 15.sp, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun WalletCard(item: WalletDisplayItem, balance: Long, logoUrl: String? = null) {
    Card(
        modifier = Modifier.width(130.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(item.color.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                if (logoUrl != null) {
                    AsyncImage(
                        model              = logoUrl,
                        contentDescription = item.method.displayName,
                        modifier           = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        contentScale       = androidx.compose.ui.layout.ContentScale.Fit,
                        error              = null,
                        placeholder        = null
                    )
                } else {
                    // Fallback emoji kalau logo belum load / CASH (tidak ada di gist)
                    Text(text = item.emoji, fontSize = 18.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.method.displayName,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF334155)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Rp ${ "%,d".format(balance).replace(',', '.') }",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (balance >= 0) item.color else Color(0xFFDC2626)
            )
        }
    }
}

@Composable
private fun InsightCard(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFEFF6FF)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(text = "💡", fontSize = 15.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = message,
                fontSize = 13.sp,
                color = Color(0xFF1E40AF),
                lineHeight = 19.sp
            )
        }
    }
}