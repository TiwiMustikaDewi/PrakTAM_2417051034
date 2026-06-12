package com.example.praktam_2417051034.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _user = mutableStateOf(auth.currentUser)
    val user: State<FirebaseUser?> = _user

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _error.value = "Email and password cannot be empty"
            return
        }
        _isLoading.value = true
        _error.value = null
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _user.value = auth.currentUser
                    onSuccess()
                } else {
                    _error.value = task.exception?.localizedMessage ?: "Login failed"
                }
            }
    }

    fun register(name: String, email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            _error.value = "All fields are required"
            return
        }
        _isLoading.value = true
        _error.value = null
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    auth.currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener { updateTask ->
                        _isLoading.value = false
                        if (updateTask.isSuccessful) {
                            _user.value = auth.currentUser
                            onSuccess()
                        } else {
                            _error.value = updateTask.exception?.localizedMessage ?: "Profile update failed"
                        }
                    }
                } else {
                    _isLoading.value = false
                    _error.value = task.exception?.localizedMessage ?: "Registration failed"
                }
            }
    }

    fun loginWithGoogle(idToken: String, onSuccess: () -> Unit) {
        _isLoading.value = true
        _error.value = null
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _user.value = auth.currentUser
                    onSuccess()
                } else {
                    _error.value = task.exception?.localizedMessage ?: "Google sign-in failed"
                }
            }
    }

    fun logout() {
        auth.signOut()
        _user.value = null
    }

    fun clearError() {
        _error.value = null
    }

    fun refreshUser() {
        _user.value = auth.currentUser
    }

    fun updateDisplayName(name: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (name.isBlank()) { onError("Name cannot be empty"); return }
        _isLoading.value = true
        val req = UserProfileChangeRequest.Builder().setDisplayName(name).build()
        auth.currentUser?.updateProfile(req)?.addOnCompleteListener { task ->
            _isLoading.value = false
            if (task.isSuccessful) {
                _user.value = auth.currentUser
                onSuccess()
            } else {
                onError(task.exception?.localizedMessage ?: "Failed to update name")
            }
        } ?: run { _isLoading.value = false; onError("No user signed in") }
    }

    fun updateEmail(newEmail: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val user = auth.currentUser
        if (newEmail.isBlank()) { onError("Email cannot be empty"); return }
        if (user == null) { onError("No user signed in"); return }
        if (newEmail.trim().equals(user.email, ignoreCase = true)) {
            onError("New email is the same as your current email")
            return
        }
        _isLoading.value = true
        // Step 1: sendEmailVerification sends to CURRENT email as safety check.
        // For changing email, Firebase requires re-auth first for security.
        // We use updateEmail directly — this works if login is recent (< 5 min).
        // If it fails with REQUIRES_RECENT_LOGIN, we tell user to re-login.
        user.updateEmail(newEmail.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Also send a verification email to the new address
                    user.sendEmailVerification()
                        .addOnCompleteListener {
                            _isLoading.value = false
                            onSuccess()
                        }
                } else {
                    _isLoading.value = false
                    val msg = task.exception?.message ?: ""
                    when {
                        msg.contains("REQUIRES_RECENT_LOGIN", ignoreCase = true) ||
                                msg.contains("recent authentication", ignoreCase = true) ->
                            onError("For security, please log out and log in again, then retry changing your email.")
                        msg.contains("already in use", ignoreCase = true) ->
                            onError("This email is already used by another account.")
                        msg.contains("badly formatted", ignoreCase = true) ||
                                msg.contains("invalid", ignoreCase = true) ->
                            onError("Invalid email format. Please check and try again.")
                        else -> onError(task.exception?.localizedMessage ?: "Failed to update email")
                    }
                }
            }
    }

    fun updatePassword(newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (newPassword.length < 6) { onError("Password must be at least 6 characters"); return }
        _isLoading.value = true
        auth.currentUser?.updatePassword(newPassword)?.addOnCompleteListener { task ->
            _isLoading.value = false
            if (task.isSuccessful) {
                onSuccess()
            } else {
                onError(task.exception?.localizedMessage ?: "Failed to update password")
            }
        } ?: run { _isLoading.value = false; onError("No user signed in") }
    }

    fun resetPassword(email: String, onEmailSent: () -> Unit) {
        if (email.isBlank()) {
            _error.value = "Silakan masukkan email terlebih dahulu"
            return
        }
        _isLoading.value = true
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _error.value = null
                    onEmailSent()
                } else {
                    _error.value = task.exception?.message ?: "Gagal mengirim email reset password"
                }
            }
    }
}