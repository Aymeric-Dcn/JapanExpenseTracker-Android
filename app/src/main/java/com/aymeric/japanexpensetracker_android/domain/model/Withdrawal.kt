package com.aymeric.japanexpensetracker_android.domain.model

data class Withdrawal(
    val id: Int = 0,
    val dateMillis: Long,
    val amountYen: Double
)