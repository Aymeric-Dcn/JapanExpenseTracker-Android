package com.aymeric.japanexpensetracker_android.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.aymeric.japanexpensetracker_android.domain.model.PaymentMethod

/**
 * A single expense.
 *
 * Amounts are whole yen stored as [Long]: the yen has no minor unit, so a floating
 * point type would only add rounding errors.
 *
 * [externalId] identifies expenses imported from an IC card history so the same
 * transaction is never imported twice. It is null for expenses entered by hand.
 */
@Entity(
    tableName = "expenses",
    indices = [Index(value = ["externalId"], unique = true)]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateMillis: Long,
    val amountYen: Long,
    val paymentMethod: PaymentMethod,
    val category: String,
    val note: String,
    val externalId: String? = null
)
