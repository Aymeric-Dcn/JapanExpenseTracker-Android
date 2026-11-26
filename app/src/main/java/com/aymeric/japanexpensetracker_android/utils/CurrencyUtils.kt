package com.aymeric.japanexpensetracker_android.utils

import java.util.Locale
import kotlin.math.abs

fun formatYen(amountYen: Long): String {
    val sign = if (amountYen < 0) "-" else ""
    return String.format(Locale.US, "%s¥%,d", sign, abs(amountYen))
}

fun formatEuro(amountYen: Long, yenToEur: Double): String =
    String.format(Locale.US, "%,.2f €", amountYen * yenToEur)

fun formatAmount(amountYen: Long, showEuro: Boolean, yenToEur: Double): String =
    if (showEuro) formatEuro(amountYen, yenToEur) else formatYen(amountYen)
