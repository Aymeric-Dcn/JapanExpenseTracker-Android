package com.aymeric.japanexpensetracker_android.presentation.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aymeric.japanexpensetracker_android.presentation.components.MenuCard
import com.aymeric.japanexpensetracker_android.presentation.navigation.Routes
import com.aymeric.japanexpensetracker_android.presentation.viewmodel.ExpenseViewModel
import com.aymeric.japanexpensetracker_android.utils.formatAmount

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: ExpenseViewModel
) {
    val expenses by viewModel.expenses.collectAsState()
    val withdrawals by viewModel.withdrawals.collectAsState()

    var showEuro by remember { mutableStateOf(false) }

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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Show in euro")
            Switch(
                checked = showEuro,
                onCheckedChange = { showEuro = it }
            )
        }

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
                    text = formatAmount(cashRemaining, showEuro),
                    style = MaterialTheme.typography.headlineLarge,
                    color = if (cashRemaining < 5000) Color(0xFFC62828) else Color(0xFF2E7D32)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Total spent",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = formatAmount(totalSpent, showEuro),
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
                Text(
                    text = if (cashRemaining < 5000)
                        "Low cash warning"
                    else
                        "Cash level is comfortable"
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