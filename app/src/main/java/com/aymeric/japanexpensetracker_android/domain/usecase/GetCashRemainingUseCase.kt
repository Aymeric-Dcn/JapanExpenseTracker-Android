package com.aymeric.japanexpensetracker_android.domain.usecase

import com.aymeric.japanexpensetracker_android.data.local.entity.ExpenseEntity
import com.aymeric.japanexpensetracker_android.data.local.entity.WithdrawalEntity
import com.aymeric.japanexpensetracker_android.domain.model.PaymentMethod

class GetCashRemainingUseCase {
    operator fun invoke(
        expenses: List<ExpenseEntity>,
        withdrawals: List<WithdrawalEntity>
    ): Long {
        val totalWithdrawals = withdrawals.sumOf { it.amountYen }
        val totalCashExpenses = expenses
            .filter { it.paymentMethod == PaymentMethod.CASH }
            .sumOf { it.amountYen }

        return totalWithdrawals - totalCashExpenses
    }
}
