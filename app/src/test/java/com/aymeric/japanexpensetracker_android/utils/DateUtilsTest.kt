package com.aymeric.japanexpensetracker_android.utils

import com.aymeric.japanexpensetracker_android.UTC
import com.aymeric.japanexpensetracker_android.utcMillis
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DateUtilsTest {

    // Thursday 26 November 2026, 18:00 UTC
    private val now = utcMillis(2026, 11, 26, hour = 18)

    @Test
    fun `day range covers midnight to midnight`() {
        val range = periodRange(PeriodFilter.DAY, now, UTC)!!
        assertEquals(utcMillis(2026, 11, 26, hour = 0), range.first)
        assertEquals(utcMillis(2026, 11, 27, hour = 0) - 1, range.last)
    }

    @Test
    fun `week starts on Monday`() {
        val range = periodRange(PeriodFilter.WEEK, now, UTC)!!
        assertEquals(utcMillis(2026, 11, 23, hour = 0), range.first)
        assertEquals(utcMillis(2026, 11, 30, hour = 0) - 1, range.last)
    }

    @Test
    fun `week containing new year spans both years`() {
        // Thursday 1 January 2026: the week started on Monday 29 December 2025.
        val newYear = utcMillis(2026, 1, 1)
        val lastYear = utcMillis(2025, 12, 29, hour = 10)

        assertTrue(isInSelectedPeriod(lastYear, PeriodFilter.WEEK, newYear, UTC))
        assertFalse(isInSelectedPeriod(lastYear, PeriodFilter.MONTH, newYear, UTC))
    }

    @Test
    fun `month range follows the calendar month`() {
        val range = periodRange(PeriodFilter.MONTH, now, UTC)!!
        assertEquals(utcMillis(2026, 11, 1, hour = 0), range.first)
        assertEquals(utcMillis(2026, 12, 1, hour = 0) - 1, range.last)
    }

    @Test
    fun `total has no range and keeps everything`() {
        assertNull(periodRange(PeriodFilter.TOTAL, now, UTC))
        assertTrue(isInSelectedPeriod(0L, PeriodFilter.TOTAL, now, UTC))
    }
}
