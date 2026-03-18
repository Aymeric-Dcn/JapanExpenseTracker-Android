package com.aymeric.japanexpensetracker_android.utils

private const val YEN_TO_EURO_RATE = 0.0062

fun formatAmount(amountYen: Double, showEuro: Boolean): String {
    return if (showEuro) {
        "%.2f €".format(amountYen * YEN_TO_EURO_RATE)
    } else {
        "%.2f ¥".format(amountYen)
    }
}