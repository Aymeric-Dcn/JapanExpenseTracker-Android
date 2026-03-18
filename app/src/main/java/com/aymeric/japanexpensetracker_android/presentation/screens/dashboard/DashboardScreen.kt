package com.aymeric.japanexpensetracker_android.presentation.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aymeric.japanexpensetracker_android.presentation.components.MenuCard
import com.aymeric.japanexpensetracker_android.presentation.navigation.Routes
import com.aymeric.japanexpensetracker_android.presentation.viewmodel.ExpenseViewModel

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: ExpenseViewModel
) {
    val expenses by viewModel.expenses.collectAsState()
    val withdrawals by viewModel.withdrawals.collectAsState()

    val totalSpent = expenses.sumOf { it.amountYen }
    val cashRemaining = viewModel.getCashRemaining()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Japan Expense Tracker",
            style = MaterialTheme.typography.headlineMedium
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Cash remaining",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "%.2f ¥".format(cashRemaining),
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (cashRemaining < 5000) Color.Red else Color(0xFF2E7D32)
                )

                Text(
                    text = "Total spent",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    text = "%.2f ¥".format(totalSpent),
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Withdrawals recorded: ${withdrawals.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        MenuCard(
            title = "Add Expense",
            onClick = { navController.navigate(Routes.AddExpense.route) }
        )

        MenuCard(
            title = "Add Cash Withdrawal",
            onClick = { navController.navigate(Routes.AddWithdrawal.route) }
        )

        MenuCard(
            title = "Expenses",
            onClick = { navController.navigate(Routes.Expenses.route) }
        )

        MenuCard(
            title = "Statistics",
            onClick = { navController.navigate(Routes.Stats.route) }
        )
    }
}