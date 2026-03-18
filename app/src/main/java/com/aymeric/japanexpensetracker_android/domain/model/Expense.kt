package com.aymeric.japanexpensetracker_android.domain.model

data class Expense(
    val id: Int = 0,
    val dateMillis: Long,
    val amountYen: Double,
    val paymentMethod: String,
    val category: String,
    val note: String
)