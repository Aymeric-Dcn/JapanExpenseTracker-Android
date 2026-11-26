package com.aymeric.japanexpensetracker_android.presentation.navigation

sealed class Routes(val route: String) {
    data object Dashboard : Routes("dashboard")
    data object AddExpense : Routes("add_expense")
    data object EditExpense : Routes("edit_expense/{$ARG_EXPENSE_ID}") {
        fun createRoute(expenseId: Int) = "edit_expense/$expenseId"
    }
    data object AddWithdrawal : Routes("add_withdrawal")
    data object Expenses : Routes("expenses")
    data object Stats : Routes("stats")
    data object ScanCard : Routes("scan_card")

    companion object {
        const val ARG_EXPENSE_ID = "expenseId"
    }
}
