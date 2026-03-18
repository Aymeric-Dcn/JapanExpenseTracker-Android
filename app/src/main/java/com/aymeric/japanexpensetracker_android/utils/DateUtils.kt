package com.aymeric.japanexpensetracker_android.utils

import java.util.Calendar

enum class PeriodFilter {
    DAY, WEEK, MONTH, TOTAL
}

fun isInSelectedPeriod(dateMillis: Long, period: PeriodFilter): Boolean {
    if (period == PeriodFilter.TOTAL) return true

    val now = Calendar.getInstance()
    val target = Calendar.getInstance().apply { timeInMillis = dateMillis }

    return when (period) {
        PeriodFilter.DAY -> {
            now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                    now.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)
        }

        PeriodFilter.WEEK -> {
            now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                    now.get(Calendar.WEEK_OF_YEAR) == target.get(Calendar.WEEK_OF_YEAR)
        }

        PeriodFilter.MONTH -> {
            now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                    now.get(Calendar.MONTH) == target.get(Calendar.MONTH)
        }

        PeriodFilter.TOTAL -> true
    }
}