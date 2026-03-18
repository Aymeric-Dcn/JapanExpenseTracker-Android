package com.aymeric.japanexpensetracker_android.domain.usecase

import com.aymeric.japanexpensetracker_android.data.local.entity.ExpenseEntity

class GetFilteredExpensesUseCase {
    operator fun invoke(
        expenses: List<ExpenseEntity>,
        category: String = "All",
        paymentMethod: String = "All"
    ): List<ExpenseEntity> {
        return expenses.filter { expense ->
            val categoryMatches = category == "All" || expense.category == category
            val paymentMatches = paymentMethod == "All" || expense.paymentMethod == paymentMethod
            categoryMatches && paymentMatches
        }
    }
}