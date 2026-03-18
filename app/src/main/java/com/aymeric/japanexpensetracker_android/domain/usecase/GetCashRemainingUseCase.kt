package com.aymeric.japanexpensetracker_android.domain.usecase

import com.aymeric.japanexpensetracker_android.data.local.entity.ExpenseEntity
import com.aymeric.japanexpensetracker_android.data.local.entity.WithdrawalEntity

class GetCashRemainingUseCase {
    operator fun invoke(
        expenses: List<ExpenseEntity>,
        withdrawals: List<WithdrawalEntity>
    ): Double {
        val totalWithdrawals = withdrawals.sumOf { it.amountYen }
        val totalCashExpenses = expenses
            .filter { it.paymentMethod == "Cash" }
            .sumOf { it.amountYen }

        return totalWithdrawals - totalCashExpenses
    }
}