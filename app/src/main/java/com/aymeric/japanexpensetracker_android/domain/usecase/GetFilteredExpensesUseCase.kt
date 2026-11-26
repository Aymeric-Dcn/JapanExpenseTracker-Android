package com.aymeric.japanexpensetracker_android.domain.usecase

import com.aymeric.japanexpensetracker_android.data.local.entity.ExpenseEntity
import com.aymeric.japanexpensetracker_android.domain.model.PaymentMethod
import com.aymeric.japanexpensetracker_android.utils.PeriodFilter
import com.aymeric.japanexpensetracker_android.utils.isInSelectedPeriod
import java.util.TimeZone

class GetFilteredExpensesUseCase {
    /**
     * @param category null keeps every category.
     * @param paymentMethod null keeps every payment method.
     */
    operator fun invoke(
        expenses: List<ExpenseEntity>,
        period: PeriodFilter = PeriodFilter.TOTAL,
        category: String? = null,
        paymentMethod: PaymentMethod? = null,
        nowMillis: Long = System.currentTimeMillis(),
        timeZone: TimeZone = TimeZone.getDefault()
    ): List<ExpenseEntity> {
        return expenses.filter { expense ->
            isInSelectedPeriod(expense.dateMillis, period, nowMillis, timeZone) &&
                (category == null || expense.category == category) &&
                (paymentMethod == null || expense.paymentMethod == paymentMethod)
        }
    }
}
