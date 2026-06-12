package com.example.praktam_2417051034.ui.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.praktam_2417051034.R
import com.example.praktam_2417051034.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val error by viewModel.error
    val isLoading by viewModel.isLoading

    val context = LocalContext.current
    
    // Configure Google Sign-In
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, gso)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                viewModel.loginWithGoogle(idToken, onLoginSuccess)
            }
        } catch (e: ApiException) {
            // handle sign-in error
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Sign in",
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

        Spacer(modifier = Modifier.height(48.dp))

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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onNavigateToForgotPassword
            ) {
                Text(
                    "Forgot Password?",
                    fontSize = 14.sp,
                    color = Color(0xFF5A66F6),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (error != null) {
            Text(
                text = error!!,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = { viewModel.login(email, password, onLoginSuccess) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A66F6)),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Google Login Button
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedButton(
            onClick = {
                val signInIntent = googleSignInClient.signInIntent
                launcher.launch(signInIntent)
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF374151)),
            border = BorderStroke(1.dp, Color(0xFFE5E7EB))
        ) {
            Text("Google", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Don't have an account? ", color = Color.Gray, fontSize = 14.sp)
            TextButton(onClick = onNavigateToRegister, contentPadding = PaddingValues(0.dp)) {
                Text(text = "Sign Up", color = Color(0xFF5A66F6), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
