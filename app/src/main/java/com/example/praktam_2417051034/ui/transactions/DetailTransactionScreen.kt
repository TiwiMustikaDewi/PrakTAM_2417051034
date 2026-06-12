package com.example.praktam_2417051034.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.praktam_2417051034.data.model.Transaction
import com.example.praktam_2417051034.data.model.TransactionType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTransactionScreen(
    transaction: Transaction,
    onBack: () -> Unit
) {
    val isIncome    = transaction.type == TransactionType.INCOME
    val amountColor = if (isIncome) Color(0xFF16A34A) else Color(0xFFDC2626)
    val amountPrefix = if (isIncome) "+ Rp" else "- Rp"
    val amountStr   = "%,d".format(transaction.amount).replace(',', '.')
    val dateStr     = SimpleDateFormat("EEEE, dd MMMM yyyy  •  HH:mm", Locale.ENGLISH)
        .format(Date(transaction.date))

    val headerGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFD6E8FF), Color(0xFFECF4FF), Color(0xFFF8F9FF))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FF))
    ) {
        // ── Header ───────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = headerGradient)
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color(0xFF1E293B))
                }
                Text(
                    "Transaction Detail",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 20.sp,
                    color      = Color(0xFF1E293B)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Amount card ───────────────────────────────────────────────────
            Surface(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(20.dp),
                color     = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier            = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Type badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = amountColor.copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier            = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment   = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                null,
                                tint     = amountColor,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                if (isIncome) "Income" else "Expense",
                                fontSize   = 12.sp,
                                color      = amountColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Amount
                    Text(
                        "$amountPrefix $amountStr",
                        fontSize   = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color      = amountColor
                    )

                    // Title
                    Text(
                        transaction.title,
                        fontSize   = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = Color(0xFF1E293B)
                    )

                    // Date
                    Text(dateStr, fontSize = 12.sp, color = Color(0xFF94A3B8))
                }
            }

            // ── Detail rows ───────────────────────────────────────────────────
            Surface(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(20.dp),
                color     = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    DetailRow(
                        icon  = Icons.Default.Category,
                        label = "Category",
                        value = if (isIncome) "Income" else transaction.expenseCategory.displayName
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color    = Color(0xFFF1F5F9)
                    )
                    DetailRow(
                        icon  = Icons.Default.AccountBalanceWallet,
                        label = if (isIncome) "Destination Wallet" else "Payment Method",
                        value = transaction.paymentMethod.displayName
                    )
                    if (transaction.note.isNotBlank()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color    = Color(0xFFF1F5F9)
                        )
                        DetailRow(
                            icon  = Icons.Default.Notes,
                            label = "Note",
                            value = transaction.note
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // ── Back button ───────────────────────────────────────────────────
            Button(
                onClick  = onBack,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Text("Back", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier            = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFEFF6FF)),
            contentAlignment    = Alignment.Center
        ) {
            Icon(icon, null, tint = Color(0xFF2563EB), modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 11.sp, color = Color(0xFF94A3B8))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E293B))
        }
    }
}