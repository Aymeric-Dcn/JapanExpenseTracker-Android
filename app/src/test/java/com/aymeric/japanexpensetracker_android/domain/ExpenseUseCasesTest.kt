package com.aymeric.japanexpensetracker_android.domain

import com.aymeric.japanexpensetracker_android.UTC
import com.aymeric.japanexpensetracker_android.domain.model.PaymentMethod
import com.aymeric.japanexpensetracker_android.domain.usecase.GetCashRemainingUseCase
import com.aymeric.japanexpensetracker_android.domain.usecase.GetFilteredExpensesUseCase
import com.aymeric.japanexpensetracker_android.expense
import com.aymeric.japanexpensetracker_android.utcMillis
import com.aymeric.japanexpensetracker_android.utils.PeriodFilter
import com.aymeric.japanexpensetracker_android.withdrawal
import org.junit.Assert.assertEquals
import org.junit.Test

class ExpenseUseCasesTest {

    @Test
    fun `only cash expenses reduce the cash remaining`() {
        val expenses = listOf(
            expense(1_200, PaymentMethod.CASH),
            expense(5_000, PaymentMethod.CARD),
            expense(330, PaymentMethod.IC_CARD),
            expense(800, PaymentMethod.CASH)
        )
        val withdrawals = listOf(withdrawal(10_000), withdrawal(5_000))

        assertEquals(13_000L, GetCashRemainingUseCase()(expenses, withdrawals))
    }

    @Test
    fun `cash remaining can go negative`() {
        assertEquals(-500L, GetCashRemainingUseCase()(listOf(expense(500)), emptyList()))
    }

    @Test
    fun `filters combine period, category and payment method`() {
        val now = utcMillis(2026, 11, 26, hour = 18)
        val today = utcMillis(2026, 11, 26, hour = 9)
        val lastMonth = utcMillis(2026, 10, 30)
        val expenses = listOf(
            expense(100, PaymentMethod.CASH, "Food", today),
            expense(200, PaymentMethod.CARD, "Food", today),
            expense(300, PaymentMethod.CASH, "Transport", today),
            expense(400, PaymentMethod.CASH, "Food", lastMonth)
        )
        val filter = GetFilteredExpensesUseCase()

        fun amounts(
            period: PeriodFilter,
            category: String? = null,
            payment: PaymentMethod? = null
        ) = filter(expenses, period, category, payment, now, UTC).map { it.amountYen }

        assertEquals(listOf(100L, 200L, 300L, 400L), amounts(PeriodFilter.TOTAL))
        assertEquals(listOf(100L, 200L, 300L), amounts(PeriodFilter.DAY))
        assertEquals(listOf(100L, 200L), amounts(PeriodFilter.MONTH, category = "Food"))
        assertEquals(listOf(100L, 400L), amounts(PeriodFilter.TOTAL, "Food", PaymentMethod.CASH))
    }
}
