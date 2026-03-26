package com.example.praktam_2417051034.model
import com.example.praktam_2417051034.R

object ExpenseData {

    private val now = System.currentTimeMillis()
    private val oneDay = 24L * 60 * 60 * 1000

    val expenseList = listOf(

        Expense("Makan Siang", 25000, "Needs", "Makan & Minum", now, R.drawable.food),
        Expense("Transport Ojek", 15000, "Needs", "Transportasi", now, R.drawable.transport),

        Expense("Beli Buku", 40000, "Needs", "Kebutuhan Pokok", now - 3 * oneDay, R.drawable.book),

        Expense("Bayar Listrik", 100000, "Needs", "Tagihan", now - 6 * oneDay, R.drawable.home),

        Expense("Beli Skincare", 75000, "Wants", "Perawatan", now - 10 * oneDay, R.drawable.shopping),

        Expense("Nonton Bioskop", 50000, "Wants", "Hiburan", now - 20 * oneDay, R.drawable.movie),
        Expense("Bukber Eclate", 28000, "Wants", "Makan & Minum", now - 22 * oneDay, R.drawable.coffee),

        Expense("Top Up Spotify", 30000, "Wants", "Hiburan", now - 40 * oneDay, R.drawable.game)
    )
}