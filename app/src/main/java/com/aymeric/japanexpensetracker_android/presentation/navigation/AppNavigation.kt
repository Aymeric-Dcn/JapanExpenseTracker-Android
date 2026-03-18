package com.aymeric.japanexpensetracker_android.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aymeric.japanexpensetracker_android.presentation.screens.addexpense.AddExpenseScreen
import com.aymeric.japanexpensetracker_android.presentation.screens.addwithdrawal.AddWithdrawalScreen
import com.aymeric.japanexpensetracker_android.presentation.screens.dashboard.DashboardScreen
import com.aymeric.japanexpensetracker_android.presentation.screens.expenses.ExpensesScreen
import com.aymeric.japanexpensetracker_android.presentation.screens.stats.StatsScreen
import com.aymeric.japanexpensetracker_android.presentation.viewmodel.ExpenseViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: ExpenseViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Dashboard.route,
        modifier = modifier
    ) {
        composable(Routes.Dashboard.route) {
            DashboardScreen(navController = navController, viewModel = viewModel)
        }

        composable(Routes.AddExpense.route) {
            AddExpenseScreen(navController = navController, viewModel = viewModel)
        }

        composable(Routes.AddWithdrawal.route) {
            AddWithdrawalScreen(navController = navController, viewModel = viewModel)
        }

        composable(Routes.Expenses.route) {
            ExpensesScreen(navController = navController, viewModel = viewModel)
        }

        composable(Routes.Stats.route) {
            StatsScreen(navController = navController, viewModel = viewModel)
        }
    }
}