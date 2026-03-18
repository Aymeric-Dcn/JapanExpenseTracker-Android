package com.aymeric.japanexpensetracker_android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "withdrawals")
data class WithdrawalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateMillis: Long,
    val amountYen: Double
)