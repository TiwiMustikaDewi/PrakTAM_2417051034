package com.example.praktam_2417051034.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.praktam_2417051034.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    val error by viewModel.error
    val isLoading by viewModel.isLoading

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                viewModel.logout()
                onRegisterSuccess()
            },
            title = { Text("Registrasi Berhasil", fontWeight = FontWeight.Bold) },
            text = { Text("Akun Anda telah berhasil dibuat. Silakan masuk menggunakan email dan kata sandi Anda.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        viewModel.logout()
                        onRegisterSuccess()
                    }
                ) {
                    Text("OK", color = Color(0xFF5A66F6), fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.padding(start = 0.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF111827))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Sign up",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Hello! Welcome Back 👋",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Full name") },
            leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = Color.Gray) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF5A66F6),
                unfocusedBorderColor = Color(0xFFE5E7EB)
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Email address") },
            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = Color.Gray) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF5A66F6),
                unfocusedBorderColor = Color(0xFFE5E7EB)
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Password") },
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = Color.Gray) },
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null, tint = Color(0xFF5A66F6))
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF5A66F6),
                unfocusedBorderColor = Color(0xFFE5E7EB)
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Confirm Password") },
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = Color.Gray) },
            trailingIcon = {
                val image = if (confirmPasswordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(imageVector = image, contentDescription = null, tint = Color(0xFF5A66F6))
                }
            },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF5A66F6),
                unfocusedBorderColor = Color(0xFFE5E7EB)
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (error != null) {
            Text(text = error!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(bottom = 16.dp))
        }

        Button(
            onClick = { 
                if (password == confirmPassword) {
                    viewModel.register(name, email, password) {
                        showSuccessDialog = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A66F6)),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Sign Up", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "By clicking Sign Up, you agree to the Terms of Services and Privacy Policy",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Already a member? ", color = Color.Gray, fontSize = 14.sp)
            TextButton(onClick = onNavigateBack, contentPadding = PaddingValues(0.dp)) {
                Text(text = "Sign In", color = Color(0xFF5A66F6), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
