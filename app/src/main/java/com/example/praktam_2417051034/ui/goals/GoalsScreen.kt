package com.example.praktam_2417051034.ui.goals

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import android.app.DatePickerDialog
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.praktam_2417051034.data.model.ExpenseCategory
import com.example.praktam_2417051034.data.model.PaymentMethod
import com.example.praktam_2417051034.data.model.Transaction
import com.example.praktam_2417051034.data.model.TransactionType
import com.example.praktam_2417051034.viewmodel.TransactionViewModel
import com.example.praktam_2417051034.data.model.FinancialGoal
import com.example.praktam_2417051034.viewmodel.GoalsViewModel
import java.text.SimpleDateFormat
import java.util.Calendar

// ── Data model ────────────────────────────────────────────────────────────────



// ── Helper ────────────────────────────────────────────────────────────────────

fun formatRp(amount: Long): String =
    "Rp %,d".format(amount).replace(',', '.')

// ── Root screen (manages sub-screens) ────────────────────────────────────────

sealed class GoalScreen {
    object List : GoalScreen()
    object AddGoal : GoalScreen()
    data class Detail(val goalId: String) : GoalScreen()
}

@Composable
fun GoalsScreen(transactionViewModel: TransactionViewModel) {
    val vm: GoalsViewModel = viewModel()
    var screen by remember { mutableStateOf<GoalScreen>(GoalScreen.List) }

    // Load dari Firestore saat pertama kali
    LaunchedEffect(Unit) { vm.loadGoals() }

    when (val s = screen) {
        is GoalScreen.List   -> GoalsListScreen(
            vm        = vm,
            onAdd     = { screen = GoalScreen.AddGoal },
            onDetail  = { screen = GoalScreen.Detail(it) }
        )
        is GoalScreen.AddGoal -> AddGoalScreen(
            onBack = { screen = GoalScreen.List },
            onSave = { goal ->
                vm.addGoal(goal) {
                    screen = GoalScreen.List
                }
            }
        )
        is GoalScreen.Detail -> {
            val goal = vm.goals.find { it.id == s.goalId }
            if (goal != null) {
                GoalDetailScreen(
                    goal          = goal,
                    onBack        = { screen = GoalScreen.List },
                    onAddProgress = { amount ->
                        vm.addProgress(goal.id, amount)
                        // Sync to dashboard: add an Income transaction to BSI
                        transactionViewModel.addTransaction(
                            Transaction(
                                title           = "Savings: ${goal.title}",
                                type            = TransactionType.INCOME,
                                amount          = amount,
                                expenseCategory = ExpenseCategory.OTHERS,
                                paymentMethod   = PaymentMethod.BSI,
                                date            = System.currentTimeMillis()
                            )
                        )
                    }
                )
            } else {
                screen = GoalScreen.List
            }
        }
    }
}

// ── 1. Goals List ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsListScreen(
    vm: GoalsViewModel,
    onAdd: () -> Unit,
    onDetail: (String) -> Unit
) {
    var showFavorites by remember { mutableStateOf(false) }
    val displayGoals  = if (showFavorites) vm.goals.filter { it.isFavorite } else vm.goals
    val favoriteCount = vm.goals.count { it.isFavorite }
    val completed = vm.goals.count { it.currentAmount >= it.targetAmount }
    val active    = vm.goals.count { it.currentAmount < it.targetAmount }

    val headerGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFD6E8FF), Color(0xFFECF4FF), Color(0xFFF8F9FF))
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FF))
        ) {
            // ── Gradient header ──────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = headerGradient)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Financial Goals", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color(0xFF1E293B))
                            Text("Track your savings targets", fontSize = 13.sp, color = Color(0xFF64748B))
                        }
                    }
                    // See Favorite Goals button
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showFavorites = !showFavorites },
                        shape = RoundedCornerShape(14.dp),
                        color = if (showFavorites) Color(0xFFFFE4E6) else Color(0xFFFFF1F2)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("❤️", fontSize = 18.sp)
                                Column {
                                    Text(
                                        if (showFavorites) "Showing Favorite Goals" else "See Favorite Goals",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize   = 14.sp,
                                        color      = Color(0xFFBE185D)
                                    )
                                    Text(
                                        "$favoriteCount goal${if (favoriteCount != 1) "s" else ""} marked as favorite",
                                        fontSize = 11.sp,
                                        color    = Color(0xFFFB7185)
                                    )
                                }
                            }
                            Icon(
                                if (showFavorites) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                null,
                                tint     = Color(0xFFBE185D),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
            LazyColumn(
                modifier       = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp, top = 12.dp)
            ) {
                // Stats header
                item {
                    GoalsHeaderCard(active = active, completed = completed, total = vm.goals.size)
                }

                // BSI savings info banner
                item {
                    BsiSavingsBanner()
                }

                if (displayGoals.isEmpty()) {
                    item {
                        if (showFavorites) {
                            // Empty favorites state
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("🤍", fontSize = 48.sp)
                                Text("No favorite goals yet", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                                Text("Tap ❤️ on any goal to mark it as favorite", fontSize = 13.sp, color = Color(0xFFB0B8C1))
                            }
                        } else {
                            EmptyGoalsState(onAdd = onAdd)
                        }
                    }
                } else {
                    item {
                        Text(
                            if (showFavorites) "❤️ Favorite Goals" else "My Goals",
                            fontWeight = FontWeight.Bold,
                            fontSize   = 16.sp,
                            color      = Color(0xFF1E293B),
                            modifier   = Modifier.padding(top = 4.dp)
                        )
                    }
                    items(displayGoals, key = { it.id }) { goal ->
                        GoalItemCard(
                            goal       = goal,
                            onClick    = { onDetail(goal.id) },
                            onFavorite = { vm.toggleFavorite(goal.id) }
                        )
                    }
                }
            }
        } // Column
        // FAB
        FloatingActionButton(
            onClick        = onAdd,
            containerColor = Color(0xFF2563EB),
            contentColor   = Color.White,
            shape          = RoundedCornerShape(14.dp),
            modifier       = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 16.dp)
        ) { Icon(Icons.Default.Add, contentDescription = "Add Goal") }
    } // Box
}

// ── 2. Add Goal ───────────────────────────────────────────────────────────────

val goalEmojis = listOf("🎯","💻","🚗","🏠","✈️","📱","🎓","💍","🏖️","💰","🏋️","🎮")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalScreen(onBack: () -> Unit, onSave: (FinancialGoal) -> Unit) {

    var title      by remember { mutableStateOf("") }
    var targetStr  by remember { mutableStateOf("") }
    var emoji      by remember { mutableStateOf("🎯") }
    var selectedDeadline by remember { mutableStateOf<Calendar?>(null) }

    val context = LocalContext.current
    val deadlineLabel = selectedDeadline?.let {
        SimpleDateFormat("dd MMMM yyyy", java.util.Locale.ENGLISH).format(it.time)
    } ?: ""

    fun showDatePicker() {
        val cal = selectedDeadline ?: Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val picked = Calendar.getInstance()
                picked.set(year, month, day)
                selectedDeadline = picked
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    val canSave = title.isNotBlank() && targetStr.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Goal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FF)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // BSI note
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFEFF6FF),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("🏦", fontSize = 20.sp)
                    Column {
                        Text(
                            "Savings allocated to BSI",
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 13.sp,
                            color      = Color(0xFF1E40AF)
                        )
                        Text(
                            "All goal savings will be counted toward your BSI account.",
                            fontSize = 11.sp,
                            color    = Color(0xFF3B82F6)
                        )
                    }
                }
            }

            // Emoji picker
            Text("Choose Icon", fontWeight = FontWeight.SemiBold, color = Color(0xFF374151))
            EmojiPicker(selected = emoji, onSelect = { emoji = it })

            // Goal name
            OutlinedTextField(
                value         = title,
                onValueChange = { title = it },
                modifier      = Modifier.fillMaxWidth(),
                label         = { Text("Goal Name") },
                placeholder   = { Text("e.g. New Laptop, Emergency Fund…") },
                shape         = RoundedCornerShape(12.dp),
                singleLine    = true,
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Color(0xFF2563EB),
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                )
            )

            // Target amount
            OutlinedTextField(
                value         = targetStr,
                onValueChange = { targetStr = it.filter { c -> c.isDigit() } },
                modifier      = Modifier.fillMaxWidth(),
                label         = { Text("Target Amount (Rp)") },
                placeholder   = { Text("e.g. 8000000") },
                shape         = RoundedCornerShape(12.dp),
                singleLine    = true,
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Color(0xFF2563EB),
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                )
            )
            if (targetStr.isNotBlank()) {
                Text(
                    "= ${formatRp(targetStr.toLongOrNull() ?: 0L)}",
                    fontSize = 12.sp,
                    color    = Color(0xFF2563EB),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // Deadline (optional) — date picker
            OutlinedTextField(
                value         = if (deadlineLabel.isNotBlank()) deadlineLabel else "",
                onValueChange = {},
                readOnly      = true,
                modifier      = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker() },
                label         = { Text("Deadline (optional)") },
                placeholder   = { Text("Tap to pick a date") },
                trailingIcon  = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (selectedDeadline != null) {
                            IconButton(onClick = { selectedDeadline = null }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Close, null, tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                            }
                        }
                        IconButton(onClick = { showDatePicker() }) {
                            Icon(Icons.Default.CalendarMonth, null, tint = Color(0xFF2563EB))
                        }
                    }
                },
                shape         = RoundedCornerShape(12.dp),
                singleLine    = true,
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Color(0xFF2563EB),
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Save
            Button(
                onClick  = {
                    if (canSave) {
                        onSave(
                            FinancialGoal(
                                title        = title.trim(),
                                targetAmount = targetStr.toLongOrNull() ?: 0L,
                                deadline     = deadlineLabel,
                                emoji        = emoji
                            )
                        )
                    }
                },
                enabled  = canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = Color(0xFF2563EB),
                    disabledContainerColor = Color(0xFFCBD5E1)
                )
            ) {
                Text("Save Goal", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

// ── 3 & 4 & 5. Goal Detail ────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    goal: FinancialGoal,
    onBack: () -> Unit,
    onAddProgress: (Long) -> Unit
) {
    var showDialog    by remember { mutableStateOf(false) }
    val progress       = (goal.currentAmount.toDouble() / goal.targetAmount).coerceIn(0.0, 1.0).toFloat()
    val pct            = (progress * 100).toInt()
    val remaining      = (goal.targetAmount - goal.currentAmount).coerceAtLeast(0L)
    val isCompleted    = goal.currentAmount >= goal.targetAmount

    val animatedProgress by animateFloatAsState(
        targetValue  = progress,
        animationSpec = tween(800),
        label        = "progress"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(goal.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FF)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Completed banner
            if (isCompleted) {
                Surface(
                    shape  = RoundedCornerShape(16.dp),
                    color  = Color(0xFFD1FAE5),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "🎉 Goal Achieved!",
                            fontWeight = FontWeight.Bold,
                            fontSize   = 18.sp,
                            color      = Color(0xFF065F46)
                        )
                    }
                }
            }

            // Big emoji + progress ring card
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(24.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier            = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(goal.emoji, fontSize = 52.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(goal.title, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF1E293B))
                    if (goal.deadline.isNotBlank()) {
                        Text(
                            "Deadline: ${goal.deadline}",
                            fontSize = 12.sp,
                            color    = Color(0xFFF59E0B)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    // Progress bar
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Progress",
                                    fontSize = 12.sp,
                                    color    = Color(0xFF94A3B8)
                                )
                                Text(
                                    "$pct%",
                                    fontSize   = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = if (isCompleted) Color(0xFF059669) else Color(0xFF2563EB)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress     = { animatedProgress },
                                modifier     = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                color        = if (isCompleted) Color(0xFF059669) else Color(0xFF2563EB),
                                trackColor   = Color(0xFFE5E7EB)
                            )
                        }
                    }
                }
            }

            // Stats cards
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GoalStatDetail(
                    label  = "Target",
                    value  = formatRp(goal.targetAmount),
                    color  = Color(0xFF2563EB),
                    modifier = Modifier.weight(1f)
                )
                GoalStatDetail(
                    label  = "Saved",
                    value  = formatRp(goal.currentAmount),
                    color  = Color(0xFF16A34A),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GoalStatDetail(
                    label  = "Remaining",
                    value  = formatRp(remaining),
                    color  = Color(0xFFF59E0B),
                    modifier = Modifier.weight(1f)
                )
                GoalStatDetail(
                    label  = "Bank",
                    value  = "🏦 BSI",
                    color  = Color(0xFF059669),
                    modifier = Modifier.weight(1f)
                )
            }

            // Add progress button (disabled if completed)
            if (!isCompleted) {
                Button(
                    onClick  = { showDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF59E0B)
                    )
                ) {
                    Text(
                        "➕  Add Progress",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp,
                        color      = Color.White
                    )
                }
            }
        }
    }

    // Add progress dialog
    if (showDialog) {
        AddProgressDialog(
            onDismiss = { showDialog = false },
            onSave    = { amount ->
                onAddProgress(amount)
                showDialog = false
            }
        )
    }
}

// ── Add Progress Dialog ───────────────────────────────────────────────────────

@Composable
fun AddProgressDialog(onDismiss: () -> Unit, onSave: (Long) -> Unit) {
    var amountStr by remember { mutableStateOf("") }
    val amount    = amountStr.toLongOrNull() ?: 0L
    val canSave   = amount > 0L

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = Color.White,
        shape            = RoundedCornerShape(20.dp),
        title = {
            Text("Add Savings", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1E293B))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Savings will be allocated to your BSI account.",
                    fontSize = 12.sp,
                    color    = Color(0xFF64748B)
                )
                OutlinedTextField(
                    value         = amountStr,
                    onValueChange = { amountStr = it.filter { c -> c.isDigit() } },
                    modifier      = Modifier.fillMaxWidth(),
                    label         = { Text("Amount (Rp)") },
                    placeholder   = { Text("e.g. 500000") },
                    shape         = RoundedCornerShape(12.dp),
                    singleLine    = true,
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Color(0xFF2563EB),
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    )
                )
                if (canSave) {
                    Text(
                        "= ${formatRp(amount)}",
                        fontSize = 12.sp,
                        color    = Color(0xFF2563EB),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick  = { if (canSave) onSave(amount) },
                enabled  = canSave,
                shape    = RoundedCornerShape(10.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = Color(0xFF2563EB),
                    disabledContainerColor = Color(0xFFCBD5E1)
                )
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF64748B))
            }
        }
    )
}

// ── Sub-composables ───────────────────────────────────────────────────────────

@Composable
fun GoalsHeaderCard(active: Int, completed: Int, total: Int) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = Color(0xFF2563EB)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF2563EB), Color(0xFF1D4ED8))
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                HeaderStat(value = "$total",     label = "Total Goals",  color = Color.White)
                VerticalDivider(modifier = Modifier.height(40.dp), color = Color.White.copy(alpha = 0.3f))
                HeaderStat(value = "$active",    label = "In Progress",  color = Color(0xFFFBBF24))
                VerticalDivider(modifier = Modifier.height(40.dp), color = Color.White.copy(alpha = 0.3f))
                HeaderStat(value = "$completed", label = "Completed",    color = Color(0xFF6EE7B7))
            }
        }
    }
}

@Composable
fun HeaderStat(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
    }
}

@Composable
fun BsiSavingsBanner() {
    Surface(
        shape    = RoundedCornerShape(14.dp),
        color    = Color(0xFFFFFBEB),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFDE68A)),
                contentAlignment = Alignment.Center
            ) {
                Text("🏦", fontSize = 20.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "All savings → BSI",
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 13.sp,
                    color      = Color(0xFF92400E)
                )
                Text(
                    "Every goal progress is allocated to your BSI account balance.",
                    fontSize    = 11.sp,
                    color       = Color(0xFFB45309),
                    lineHeight  = 15.sp
                )
            }
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint     = Color(0xFFF59E0B),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun GoalItemCard(goal: FinancialGoal, onClick: () -> Unit, onFavorite: () -> Unit = {}) {
    val progress    = (goal.currentAmount.toDouble() / goal.targetAmount).coerceIn(0.0, 1.0).toFloat()
    val pct         = (progress * 100).toInt()
    val isCompleted = goal.currentAmount >= goal.targetAmount

    val animatedProgress by animateFloatAsState(
        targetValue   = progress,
        animationSpec = tween(600),
        label         = "goalProgress"
    )

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment    = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier             = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(goal.emoji, fontSize = 22.sp)
                    }
                    Column {
                        Text(
                            goal.title,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 15.sp,
                            color      = Color(0xFF1E293B),
                            maxLines   = 1,
                            overflow   = TextOverflow.Ellipsis
                        )
                        if (goal.deadline.isNotBlank()) {
                            Text(
                                "📅 ${goal.deadline}",
                                fontSize = 11.sp,
                                color    = Color(0xFFF59E0B)
                            )
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Favorite toggle button
                    IconButton(
                        onClick  = { onFavorite() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            if (goal.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint     = if (goal.isFavorite) Color(0xFFE11D48) else Color(0xFFCBD5E1),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    if (isCompleted) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Completed",
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Text(
                    "$pct%",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 14.sp,
                    color      = Color(0xFF2563EB)
                )

            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress     = { animatedProgress },
                modifier     = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color        = if (isCompleted) Color(0xFF059669) else Color(0xFF2563EB),
                trackColor   = Color(0xFFE5E7EB)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    formatRp(goal.currentAmount),
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color      = Color(0xFF1E293B)
                )
                Text(
                    "of ${formatRp(goal.targetAmount)}",
                    fontSize = 12.sp,
                    color    = Color(0xFF94A3B8)
                )
            }
        }
    }
}

@Composable
fun GoalStatDetail(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(label, fontSize = 11.sp, color = Color(0xFF94A3B8))
            Text(
                value,
                fontSize   = 14.sp,
                fontWeight = FontWeight.Bold,
                color      = color,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun EmojiPicker(selected: String, onSelect: (String) -> Unit) {
    val rows = goalEmojis.chunked(6)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { e ->
                    val isSelected = e == selected
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color(0xFFDBEAFE) else Color(0xFFF1F5F9))
                            .clickable { onSelect(e) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(e, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyGoalsState(onAdd: () -> Unit) {
    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("🎯", fontSize = 56.sp)
        Text(
            "No Goals Yet",
            fontWeight = FontWeight.Bold,
            fontSize   = 18.sp,
            color      = Color(0xFF1E293B)
        )
        Text(
            "Tap + to set your first financial goal\nand start saving toward it!",
            fontSize   = 14.sp,
            color      = Color(0xFF94A3B8),
            textAlign  = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick  = onAdd,
            shape    = RoundedCornerShape(12.dp),
            colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Add First Goal", fontWeight = FontWeight.Bold)
        }
    }
}