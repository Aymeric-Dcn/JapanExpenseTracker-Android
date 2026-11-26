package com.aymeric.japanexpensetracker_android.domain

import com.aymeric.japanexpensetracker_android.UTC
import com.aymeric.japanexpensetracker_android.domain.model.PaymentMethod
import com.aymeric.japanexpensetracker_android.domain.usecase.DemoData
import com.aymeric.japanexpensetracker_android.utcMillis
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DemoDataTest {

    private val now = utcMillis(2026, 11, 26, hour = 20)

    @Test
    fun `demo data is deterministic for a given day`() {
        assertEquals(DemoData.generate(now, UTC), DemoData.generate(now, UTC))
    }

    @Test
    fun `demo data looks like real use`() {
        val data = DemoData.generate(now, UTC, days = 21)

        assertTrue(data.expenses.size > 40)
        assertTrue(data.expenses.all { it.amountYen > 0 && it.dateMillis <= now })
        assertTrue(data.expenses.any { it.dateMillis >= utcMillis(2026, 11, 26, hour = 0) })
        assertEquals(PaymentMethod.entries.toSet(), data.expenses.map { it.paymentMethod }.toSet())
        assertEquals(data.expenses.size, data.expenses.map { it.externalId }.toSet().size)
        assertEquals(2, data.withdrawals.size)
    }
}
