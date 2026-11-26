package com.aymeric.japanexpensetracker_android.utils

import java.util.Calendar
import java.util.TimeZone

enum class PeriodFilter {
    DAY, WEEK, MONTH, TOTAL
}

/**
 * Returns the [start, end) range in epoch millis of the period that contains [nowMillis],
 * or null for [PeriodFilter.TOTAL]. Weeks start on Monday.
 */
fun periodRange(
    period: PeriodFilter,
    nowMillis: Long,
    timeZone: TimeZone = TimeZone.getDefault()
): LongRange? {
    if (period == PeriodFilter.TOTAL) return null

    val start = Calendar.getInstance(timeZone).apply {
        timeInMillis = nowMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        when (period) {
            PeriodFilter.WEEK -> {
                val daysSinceMonday = (get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7
                add(Calendar.DAY_OF_MONTH, -daysSinceMonday)
            }
            PeriodFilter.MONTH -> set(Calendar.DAY_OF_MONTH, 1)
            else -> Unit
        }
    }

    val end = (start.clone() as Calendar).apply {
        when (period) {
            PeriodFilter.DAY -> add(Calendar.DAY_OF_MONTH, 1)
            PeriodFilter.WEEK -> add(Calendar.DAY_OF_MONTH, 7)
            PeriodFilter.MONTH -> add(Calendar.MONTH, 1)
            PeriodFilter.TOTAL -> Unit
        }
    }

    return start.timeInMillis until end.timeInMillis
}

fun isInSelectedPeriod(
    dateMillis: Long,
    period: PeriodFilter,
    nowMillis: Long = System.currentTimeMillis(),
    timeZone: TimeZone = TimeZone.getDefault()
): Boolean {
    val range = periodRange(period, nowMillis, timeZone) ?: return true
    return dateMillis in range
}
