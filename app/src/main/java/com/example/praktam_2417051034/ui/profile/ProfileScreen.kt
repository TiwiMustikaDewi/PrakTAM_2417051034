package com.example.praktam_2417051034.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.praktam_2417051034.data.model.TransactionType
import com.example.praktam_2417051034.viewmodel.AuthViewModel
import com.example.praktam_2417051034.viewmodel.TransactionViewModel
import com.example.praktam_2417051034.ui.dashboard.computeHealthScore
import com.example.praktam_2417051034.ui.profile.ProfilePhotoFromFirestore

// ── Achievement model ──────────────────────────────────────────────────────────
data class Achievement(
    val emoji: String,
    val label: String,
    val description: String,
    val unlocked: Boolean
)

// ── Compute achievements from real transaction data ────────────────────────────
fun computeAchievements(vm: TransactionViewModel): List<Achievement> {
    val txList        = vm.transactions
    val totalTx       = txList.size
    val incomeCount   = txList.count { it.type == TransactionType.INCOME }
    val totalIncome   = vm.totalIncome
    val totalExpense  = vm.totalExpense
    val distinctWallets = txList.map { it.paymentMethod }.distinct().size
    val expenseCount  = txList.count { it.type == TransactionType.EXPENSE }

    return listOf(
        Achievement("🎯", "First Step",    "Record your first transaction",          totalTx >= 1),
        Achievement("💰", "First Income",  "Record your first income",               incomeCount >= 1),
        Achievement("📊", "Getting Real",  "Record 5 transactions",                  totalTx >= 5),
        Achievement("🏆", "Power User",    "Record 30 transactions",                 totalTx >= 30),
        Achievement("💼", "Multi-Wallet",  "Use 2 or more different wallets",        distinctWallets >= 2),
        Achievement("📈", "Big Saver",     "Keep income greater than expenses",      totalIncome > totalExpense && totalIncome > 0),
        Achievement("🌟", "Consistent",    "Record 10 or more transactions",         totalTx >= 10),
        Achievement("🛒", "Shopper",       "Record 5 expense transactions",          expenseCount >= 5),
    )
}

// ── Profile Screen ─────────────────────────────────────────────────────────────
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    transactionViewModel: TransactionViewModel,
    onLogout: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToHelpFaq: () -> Unit
) {
    val firebaseUser by authViewModel.user
    val achievements  = computeAchievements(transactionViewModel)
    val unlockedCount = achievements.count { it.unlocked }

    val headerGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFD6E8FF), Color(0xFFECF4FF), Color(0xFFF8F9FF))
    )

    // Financial health score — pakai fungsi yang sama dengan DashboardScreen
    val healthScore = computeHealthScore(transactionViewModel.totalIncome, transactionViewModel.totalExpense)
    val healthLabel = when {
        healthScore >= 75 -> "Excellent Condition"
        healthScore >= 50 -> "Good Condition"
        healthScore >= 30 -> "Fair Condition"
        else              -> "Needs Attention"
    }
    val healthColor = when {
        healthScore >= 75 -> Color(0xFF2563EB)
        healthScore >= 50 -> Color(0xFF16A34A)
        healthScore >= 30 -> Color(0xFFF97316)
        else              -> Color(0xFFDC2626)
    }

    var logoutDialogVisible by remember { mutableStateOf(false) }

    // Logout confirmation dialog
    if (logoutDialogVisible) {
        AlertDialog(
            onDismissRequest = { logoutDialogVisible = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Text("Logout", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            },
            text = {
                Text(
                    "Are you sure you want to logout from TIVO?",
                    color = Color(0xFF475569),
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { logoutDialogVisible = false; onLogout() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Logout", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { logoutDialogVisible = false }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FF))
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── HEADER ─────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = headerGradient)
                .padding(horizontal = 20.dp, vertical = 28.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                // Avatar — load from Firestore, re-fetch every time screen is shown
                var photoKey by remember { mutableStateOf(0) }
                LaunchedEffect(Unit) { photoKey++ }
                ProfilePhotoFromFirestore(size = 96.dp, iconSize = 58.dp, refreshKey = photoKey)

                Spacer(modifier = Modifier.height(14.dp))

                // Name & email
                Text(
                    text = firebaseUser?.displayName ?: "User",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = firebaseUser?.email ?: "",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Edit Profile button
                OutlinedButton(
                    onClick = onNavigateToEditProfile,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2563EB)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF2563EB)),
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit Profile", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStat(
                        value  = transactionViewModel.transactions.size.toString(),
                        label  = "Transactions",
                        color  = Color(0xFF2563EB)
                    )
                    Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color(0xFFCBD5E1)))
                    ProfileStat(
                        value  = "$unlockedCount/${achievements.size}",
                        label  = "Achievements",
                        color  = Color(0xFFD97706)
                    )
                    Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color(0xFFCBD5E1)))
                    ProfileStat(
                        value  = "$healthScore",
                        label  = "Health Score",
                        color  = healthColor
                    )
                }
            }
        }

        // ── BODY ───────────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // ── Financial Health Card ─────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { healthScore / 100f },
                            modifier = Modifier.size(68.dp),
                            color = healthColor,
                            trackColor = healthColor.copy(alpha = 0.12f),
                            strokeWidth = 7.dp
                        )
                        Text(
                            "$healthScore",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = healthColor
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Column {
                        Text(
                            "Financial Health Score",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            healthLabel,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { healthScore / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = healthColor,
                            trackColor = healthColor.copy(alpha = 0.12f)
                        )
                    }
                }
            }

            // ── Achievements ──────────────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Achievements",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = Color(0xFF1E293B)
                    )
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFFEF9C3)
                    ) {
                        Text(
                            "$unlockedCount unlocked",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            color = Color(0xFFD97706),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // First row of achievements (4 items)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    achievements.take(4).forEach { ach ->
                        AchievementCard(ach, Modifier.weight(1f))
                    }
                }
                // Second row (remaining 4)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    achievements.drop(4).forEach { ach ->
                        AchievementCard(ach, Modifier.weight(1f))
                    }
                }
            }

            // ── Settings ──────────────────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "Settings",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(4.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        SettingsItem(
                            icon       = Icons.Outlined.Notifications,
                            iconBg     = Color(0xFFEFF6FF),
                            iconColor  = Color(0xFF2563EB),
                            title      = "Notifications",
                            subtitle   = "View your activity alerts",
                            showDivider = true,
                            onClick    = onNavigateToNotifications
                        )
                        SettingsItem(
                            icon       = Icons.Default.Info,
                            iconBg     = Color(0xFFEDE9FE),
                            iconColor  = Color(0xFF7C3AED),
                            title      = "About TIVO",
                            subtitle   = "App info & version",
                            showDivider = true,
                            onClick    = onNavigateToAbout
                        )
                        SettingsItem(
                            icon       = Icons.Default.Help,
                            iconBg     = Color(0xFFF0FDF4),
                            iconColor  = Color(0xFF16A34A),
                            title      = "Help & FAQ",
                            subtitle   = "Get answers to common questions",
                            showDivider = false,
                            onClick    = onNavigateToHelpFaq
                        )
                    }
                }
            }

            // ── Logout ────────────────────────────────────────────────────────
            Button(
                onClick = { logoutDialogVisible = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = Color(0xFFDC2626)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Logout",
                    color = Color(0xFFDC2626),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// ── Sub-composables ────────────────────────────────────────────────────────────

@Composable
private fun ProfileStat(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = color)
        Text(label, fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Medium)
    }
}

@Composable
fun AchievementCard(achievement: Achievement, modifier: Modifier) {
    val bg    = if (achievement.unlocked) Color(0xFFFFFBEB) else Color(0xFFF8FAFC)
    val alpha = if (achievement.unlocked) 1f else 0.35f

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(defaultElevation = if (achievement.unlocked) 2.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
                    .background(
                        if (achievement.unlocked) Color(0xFFFEF3C7)
                        else Color(0xFFF1F5F9)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = achievement.emoji,
                    fontSize = 22.sp,
                    modifier = Modifier.graphicsLayer(alpha = alpha)
                )
                if (!achievement.unlocked) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(2.dp)
                            .size(13.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color(0xFFCBD5E1)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(8.dp),
                            tint = Color(0xFF64748B)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = achievement.label,
                fontSize = 9.sp,
                color = if (achievement.unlocked) Color(0xFF92400E) else Color(0xFFCBD5E1),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 13.sp
            )
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    title: String,
    subtitle: String,
    showDivider: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent,
        onClick = onClick
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color(0xFF1E293B))
                    Text(subtitle, fontSize = 12.sp, color = Color(0xFF94A3B8))
                }
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFFCBD5E1),
                    modifier = Modifier.size(20.dp)
                )
            }
            if (showDivider) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    color = Color(0xFFF1F5F9)
                )
            }
        }
    }
}