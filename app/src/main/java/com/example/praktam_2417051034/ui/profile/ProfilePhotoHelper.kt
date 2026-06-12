package com.example.praktam_2417051034.ui.profile

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// ── Helper: load photo from Firestore and render ──────────────────────────────

@Composable
fun ProfilePhotoFromFirestore(
    size: Dp = 48.dp,
    iconSize: Dp = 32.dp,
    modifier: Modifier = Modifier,
    refreshKey: Int = 0   // bump this to force re-fetch after upload
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(uid, refreshKey) {
        bitmap = null  // clear while re-loading
        if (uid == null) return@LaunchedEffect
        FirebaseFirestore.getInstance()
            .collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val b64 = doc.getString("photoBase64") ?: return@addOnSuccessListener
                runCatching {
                    // Strip data URI prefix if present
                    val pure = if (b64.contains(",")) b64.substringAfter(",") else b64
                    val bytes = Base64.decode(pure, Base64.NO_WRAP)
                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                }
            }
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFFBFDBFE)),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap          = bitmap!!,
                contentDescription = "Profile photo",
                modifier        = Modifier.fillMaxSize().clip(CircleShape),
                contentScale    = ContentScale.Crop
            )
        } else {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint     = Color(0xFF2563EB),
                modifier = Modifier.size(iconSize)
            )
        }
    }
}