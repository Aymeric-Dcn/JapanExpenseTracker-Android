package com.aymeric.japanexpensetracker_android.presentation.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.aymeric.japanexpensetracker_android.presentation.components.CurrencyToggle
import com.aymeric.japanexpensetracker_android.presentation.components.MenuCard
import com.aymeric.japanexpensetracker_android.presentation.navigation.Routes
import com.aymeric.japanexpensetracker_android.presentation.viewmodel.ExpenseViewModel
import com.aymeric.japanexpensetracker_android.utils.formatAmount

private const val LOW_CASH_THRESHOLD_YEN = 5_000L

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: ExpenseViewModel
) {
    val expenses by viewModel.expenses.collectAsState()
    val withdrawals by viewModel.withdrawals.collectAsState()
    val showEuro by viewModel.showEuro.collectAsState()
    val rate by viewModel.exchangeRate.collectAsState()

    val totalSpent = expenses.sumOf { it.amountYen }
    val cashRemaining = viewModel.getCashRemaining()
    val lowCash = cashRemaining < LOW_CASH_THRESHOLD_YEN

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Japan Expense Tracker",
            style = MaterialTheme.typography.headlineMedium
        )

        CurrencyToggle(
            showEuro = showEuro,
            onShowEuroChange = viewModel::setShowEuro,
            rate = rate
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Cash remaining",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = formatAmount(cashRemaining, showEuro, rate.yenToEur),
                    style = MaterialTheme.typography.headlineLarge,
                    color = if (lowCash) Color(0xFFC62828) else Color(0xFF2E7D32)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Total spent",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = formatAmount(totalSpent, showEuro, rate.yenToEur),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Quick summary",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Expenses recorded: ${expenses.size}")
                Text("Withdrawals recorded: ${withdrawals.size}")
                Text(if (lowCash) "Low cash warning" else "Cash level is comfortable")
            }
        }

        MenuCard(
            title = "Add Expense",
            onClick = { navController.navigate(Routes.AddExpense.route) }
        )

        MenuCard(
            title = "Scan Suica / Pasmo",
            onClick = { navController.navigate(Routes.ScanCard.route) }
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
