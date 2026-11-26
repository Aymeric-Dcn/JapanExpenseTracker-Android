package com.aymeric.japanexpensetracker_android

import com.aymeric.japanexpensetracker_android.data.local.entity.ExpenseEntity
import com.aymeric.japanexpensetracker_android.data.local.entity.WithdrawalEntity
import com.aymeric.japanexpensetracker_android.domain.model.PaymentMethod
import java.util.Calendar
import java.util.TimeZone

val UTC: TimeZone = TimeZone.getTimeZone("UTC")

fun utcMillis(year: Int, month: Int, day: Int, hour: Int = 12, minute: Int = 0): Long =
    Calendar.getInstance(UTC).apply {
        clear()
        set(year, month - 1, day, hour, minute, 0)
    }.timeInMillis

fun expense(
    amountYen: Long,
    paymentMethod: PaymentMethod = PaymentMethod.CASH,
    category: String = "Food",
    dateMillis: Long = utcMillis(2026, 11, 26),
    note: String = "",
    externalId: String? = null
) = ExpenseEntity(
    dateMillis = dateMillis,
    amountYen = amountYen,
    paymentMethod = paymentMethod,
    category = category,
    note = note,
    externalId = externalId
)

fun withdrawal(amountYen: Long) = WithdrawalEntity(dateMillis = utcMillis(2026, 11, 20), amountYen = amountYen)
