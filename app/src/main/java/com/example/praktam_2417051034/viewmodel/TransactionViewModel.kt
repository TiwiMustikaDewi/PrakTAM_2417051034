package com.example.praktam_2417051034.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.praktam_2417051034.data.model.ExpenseCategory
import com.example.praktam_2417051034.data.model.PaymentMethod
import com.example.praktam_2417051034.data.model.Transaction
import com.example.praktam_2417051034.data.model.TransactionType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class TransactionViewModel : ViewModel() {

    private val auth      = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    val transactions = mutableStateListOf<Transaction>()
    var isLoading    = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    private fun col() = auth.currentUser?.uid?.let { uid ->
        firestore.collection("users").document(uid).collection("transactions")
    }

    // ── Load ──────────────────────────────────────────────────────────────────
    fun loadTransactions() {
        val c = col() ?: return
        isLoading.value = true
        c.orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snap ->
                transactions.clear()
                snap.documents.forEach { doc ->
                    runCatching {
                        transactions.add(
                            Transaction(
                                id              = doc.id,
                                title           = doc.getString("title") ?: "",
                                type            = TransactionType.valueOf(doc.getString("type") ?: "EXPENSE"),
                                amount          = doc.getLong("amount") ?: 0L,
                                expenseCategory = ExpenseCategory.valueOf(doc.getString("expenseCategory") ?: "OTHERS"),
                                paymentMethod   = PaymentMethod.valueOf(doc.getString("paymentMethod") ?: "CASH"),
                                date            = doc.getLong("date") ?: System.currentTimeMillis(),
                                note            = doc.getString("note") ?: ""
                            )
                        )
                    }
                }
                isLoading.value = false
            }
            .addOnFailureListener { e ->
                errorMessage.value = e.message
                isLoading.value = false
            }
    }

    // ── Add ───────────────────────────────────────────────────────────────────
    fun addTransaction(transaction: Transaction, onDone: () -> Unit = {}) {
        val c = col() ?: run { transactions.add(0, transaction); onDone(); return }
        val data = mapOf(
            "title"           to transaction.title,
            "type"            to transaction.type.name,
            "amount"          to transaction.amount,
            "expenseCategory" to transaction.expenseCategory.name,
            "paymentMethod"   to transaction.paymentMethod.name,
            "date"            to transaction.date,
            "note"            to transaction.note
        )
        c.document(transaction.id).set(data)
            .addOnSuccessListener {
                // Insert at correct position sorted by date desc
                val idx = transactions.indexOfFirst { it.date <= transaction.date }
                if (idx == -1) transactions.add(transaction) else transactions.add(idx, transaction)
                onDone()
            }
            .addOnFailureListener { e -> errorMessage.value = e.message; onDone() }
    }

    // ── Update ────────────────────────────────────────────────────────────────
    fun updateTransaction(transaction: Transaction, onDone: () -> Unit = {}) {
        val c = col() ?: run {
            val idx = transactions.indexOfFirst { it.id == transaction.id }
            if (idx >= 0) transactions[idx] = transaction
            onDone(); return
        }
        val data = mapOf(
            "title"           to transaction.title,
            "type"            to transaction.type.name,
            "amount"          to transaction.amount,
            "expenseCategory" to transaction.expenseCategory.name,
            "paymentMethod"   to transaction.paymentMethod.name,
            "date"            to transaction.date,
            "note"            to transaction.note
        )
        c.document(transaction.id).set(data)
            .addOnSuccessListener {
                val idx = transactions.indexOfFirst { it.id == transaction.id }
                if (idx >= 0) transactions[idx] = transaction
                onDone()
            }
            .addOnFailureListener { e -> errorMessage.value = e.message; onDone() }
    }

    // ── Delete ────────────────────────────────────────────────────────────────
    fun deleteTransaction(transaction: Transaction) {
        val c = col() ?: run { transactions.remove(transaction); return }
        c.document(transaction.id).delete()
            .addOnSuccessListener { transactions.remove(transaction) }
            .addOnFailureListener { e -> errorMessage.value = e.message }
    }

    // ── Totals ────────────────────────────────────────────────────────────────
    val totalIncome: Long  get() = transactions.filter { it.type == TransactionType.INCOME  }.sumOf { it.amount }
    val totalExpense: Long get() = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

    fun walletBalance(method: PaymentMethod): Long {
        val inc = transactions.filter { it.type == TransactionType.INCOME  && it.paymentMethod == method }.sumOf { it.amount }
        val exp = transactions.filter { it.type == TransactionType.EXPENSE && it.paymentMethod == method }.sumOf { it.amount }
        return inc - exp
    }
}