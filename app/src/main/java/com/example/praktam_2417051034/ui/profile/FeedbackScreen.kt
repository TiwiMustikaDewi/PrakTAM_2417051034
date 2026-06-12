package com.example.praktam_2417051034.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

// ── Data ─────────────────────────────────────────────────────────────────────

private data class FeedbackCategory(
    val label: String,
    val icon: ImageVector,
    val color: Color
)

private val feedbackCategories = listOf(
    FeedbackCategory("Bug Report",    Icons.Default.BugReport,       Color(0xFFEF4444)),
    FeedbackCategory("Feature Request", Icons.Default.Lightbulb,     Color(0xFFF59E0B)),
    FeedbackCategory("UI/UX",         Icons.Default.Palette,         Color(0xFF8B5CF6)),
    FeedbackCategory("Performance",   Icons.Default.Speed,           Color(0xFF06B6D4)),
    FeedbackCategory("General",       Icons.Default.ChatBubbleOutline, Color(0xFF2563EB))
)

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun FeedbackScreen(onBack: () -> Unit) {
    val auth      = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser

    var selectedCategory by remember { mutableStateOf<FeedbackCategory?>(null) }
    var rating           by remember { mutableStateOf(0) }
    var feedbackText     by remember { mutableStateOf("") }
    var isSubmitting     by remember { mutableStateOf(false) }
    var submitSuccess    by remember { mutableStateOf(false) }
    var errorMessage     by remember { mutableStateOf<String?>(null) }

    val headerGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFD6E8FF), Color(0xFFECF4FF), Color(0xFFF8F9FF))
    )

    // ── Blur success popup ─────────────────────────────────────────────────────
    if (submitSuccess) {
        Dialog(
            onDismissRequest = onBack,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape  = RoundedCornerShape(24.dp),
                    color  = Color.White,
                    modifier = Modifier
                        .padding(32.dp)
                        .width(300.dp)
                        .clickable(enabled = false, onClick = {})
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2563EB).copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2563EB), modifier = Modifier.size(40.dp))
                        }
                        Text("Feedback Sent! 🎉", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), textAlign = TextAlign.Center)
                        Text(
                            "Thank you! Your feedback has been received and will help us improve TIVO.",
                            fontSize = 13.sp, color = Color(0xFF64748B), textAlign = TextAlign.Center, lineHeight = 20.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Button(
                            onClick = onBack,
                            shape   = RoundedCornerShape(14.dp),
                            colors  = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) { Text("Back to Help & FAQ", fontWeight = FontWeight.SemiBold) }
                    }
                }
            }
        }
    }

    // ── Loading overlay ────────────────────────────────────────────────────────
    if (isSubmitting) {
        Dialog(onDismissRequest = {}, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(shape = RoundedCornerShape(20.dp), color = Color.White, modifier = Modifier.size(160.dp)) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF2563EB), strokeWidth = 3.dp, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Sending…", fontSize = 13.sp, color = Color(0xFF64748B))
                    }
                }
            }
        }
    }

    // ── Main content ───────────────────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FF))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = headerGradient)
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E293B)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            "Send Feedback",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            "Help us improve TIVO",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }

        // Scrollable form
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Category picker ────────────────────────────────────────────────
            Text(
                "Feedback Category",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            )
            feedbackCategories.chunked(3).forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowItems.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedCategory = cat }
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) cat.color else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) cat.color.copy(alpha = 0.08f) else Color.White
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    cat.icon, null,
                                    tint = if (isSelected) cat.color else Color(0xFF94A3B8),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    cat.label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected) cat.color else Color(0xFF64748B),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                    // fill empty space if row has < 3 items
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // ── Star rating ────────────────────────────────────────────────────
            Text(
                "Overall Rating",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            )
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        (1..5).forEach { star ->
                            Icon(
                                if (star <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = if (star <= rating) Color(0xFFF59E0B) else Color(0xFFCBD5E1),
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { rating = star }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        when (rating) {
                            1 -> "Poor 😞"
                            2 -> "Fair 😐"
                            3 -> "Good 🙂"
                            4 -> "Great 😊"
                            5 -> "Excellent 🤩"
                            else -> "Tap a star to rate"
                        },
                        fontSize = 13.sp,
                        color = if (rating > 0) Color(0xFF1E293B) else Color(0xFF94A3B8),
                        fontWeight = if (rating > 0) FontWeight.Medium else FontWeight.Normal
                    )
                }
            }

            // ── Feedback text ──────────────────────────────────────────────────
            Text(
                "Your Feedback",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            )
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                OutlinedTextField(
                    value = feedbackText,
                    onValueChange = { if (it.length <= 500) feedbackText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 140.dp),
                    placeholder = {
                        Text(
                            "Describe your experience, a bug you found, or a feature you'd love to see…",
                            fontSize = 13.sp,
                            color = Color(0xFFCBD5E1),
                            lineHeight = 20.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Color(0xFF2563EB),
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor   = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    maxLines = 10
                )
            }
            Text(
                "${feedbackText.length}/500",
                fontSize = 11.sp,
                color = if (feedbackText.length > 450) Color(0xFFEF4444) else Color(0xFF94A3B8),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )

            // ── Error message ──────────────────────────────────────────────────
            errorMessage?.let {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEE2E2)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.ErrorOutline, null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                        Text(it, fontSize = 12.sp, color = Color(0xFFB91C1C))
                    }
                }
            }

            // ── Submit button ──────────────────────────────────────────────────
            val canSubmit = selectedCategory != null && rating > 0 && feedbackText.isNotBlank()

            Button(
                onClick = {
                    errorMessage = null
                    if (!canSubmit) {
                        errorMessage = "Please select a category, give a rating, and write your feedback."
                        return@Button
                    }
                    isSubmitting = true

                    val feedbackData = hashMapOf(
                        "userId"    to (currentUser?.uid ?: "anonymous"),
                        "userEmail" to (currentUser?.email ?: "anonymous"),
                        "category"  to selectedCategory!!.label,
                        "rating"    to rating,
                        "message"   to feedbackText.trim(),
                        "timestamp" to FieldValue.serverTimestamp()
                    )

                    firestore.collection("feedbacks")
                        .add(feedbackData)
                        .addOnSuccessListener {
                            isSubmitting  = false
                            submitSuccess = true
                        }
                        .addOnFailureListener { e ->
                            isSubmitting = false
                            errorMessage = "Failed to send feedback: ${e.message}"
                        }
                },
                enabled  = canSubmit && !isSubmitting,
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = Color(0xFF2563EB),
                    disabledContainerColor = Color(0xFFCBD5E1)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("Sending…", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                } else {
                    Icon(Icons.Default.Send, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Send Feedback", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}