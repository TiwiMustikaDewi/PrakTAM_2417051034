package com.example.praktam_2417051034.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.praktam_2417051034.data.model.FinancialGoal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GoalsViewModel : ViewModel() {
    private val auth      = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    val goals        = mutableStateListOf<FinancialGoal>()
    var isLoading    = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    private fun col() = auth.currentUser?.uid?.let { uid ->
        firestore.collection("users").document(uid).collection("goals")
    }

    fun loadGoals() {
        val c = col() ?: return
        isLoading.value = true
        c.get()
            .addOnSuccessListener { snap ->
                goals.clear()
                snap.documents.forEach { doc ->
                    runCatching {
                        goals.add(
                            FinancialGoal(
                                id            = doc.id,
                                title         = doc.getString("title") ?: "",
                                targetAmount  = doc.getLong("targetAmount") ?: 0L,
                                currentAmount = doc.getLong("currentAmount") ?: 0L,
                                deadline      = doc.getString("deadline") ?: "",
                                emoji         = doc.getString("emoji") ?: "🎯",
                                isFavorite    = doc.getBoolean("isFavorite") ?: false
                            )
                        )
                    }
                }
                isLoading.value = false
            }
            .addOnFailureListener { e -> errorMessage.value = e.message; isLoading.value = false }
    }

    fun addGoal(goal: FinancialGoal, onDone: () -> Unit = {}) {
        val c = col() ?: run { goals.add(0, goal); onDone(); return }
        val data = mapOf(
            "title"         to goal.title,
            "targetAmount"  to goal.targetAmount,
            "currentAmount" to goal.currentAmount,
            "deadline"      to goal.deadline,
            "emoji"         to goal.emoji,
            "isFavorite"    to goal.isFavorite
        )
        c.document(goal.id).set(data)
            .addOnSuccessListener { goals.add(0, goal); onDone() }
            .addOnFailureListener { e -> errorMessage.value = e.message; onDone() }
    }

    fun deleteGoal(goalId: String) {
        val c = col() ?: run { goals.removeAll { it.id == goalId }; return }
        c.document(goalId).delete()
            .addOnSuccessListener { goals.removeAll { it.id == goalId } }
            .addOnFailureListener { e -> errorMessage.value = e.message }
    }

    fun addProgress(goalId: String, amount: Long) {
        val idx = goals.indexOfFirst { it.id == goalId }
        if (idx < 0) return
        val g      = goals[idx]
        val newAmt = (g.currentAmount + amount).coerceAtMost(g.targetAmount)
        goals[idx] = g.copy(currentAmount = newAmt)
        col()?.document(goalId)?.update("currentAmount", newAmt)
    }

    fun toggleFavorite(goalId: String) {
        val idx = goals.indexOfFirst { it.id == goalId }
        if (idx < 0) return
        val newVal = !goals[idx].isFavorite
        goals[idx] = goals[idx].copy(isFavorite = newVal)
        col()?.document(goalId)?.update("isFavorite", newVal)
    }
}