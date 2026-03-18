package com.aymeric.japanexpensetracker_android.presentation.navigation

sealed class Routes(val route: String) {
    data object Dashboard : Routes("dashboard")
    data object AddExpense : Routes("add_expense")
    data object AddWithdrawal : Routes("add_withdrawal")
    data object Expenses : Routes("expenses")
    data object Stats : Routes("stats")
}