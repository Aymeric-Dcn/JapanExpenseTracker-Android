package com.aymeric.japanexpensetracker_android.presentation.screens.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aymeric.japanexpensetracker_android.presentation.viewmodel.ExpenseViewModel
import com.aymeric.japanexpensetracker_android.utils.formatAmount

@Composable
fun StatsScreen(
    navController: NavController,
    viewModel: ExpenseViewModel
) {
    val expenses by viewModel.expenses.collectAsState()
    val withdrawals by viewModel.withdrawals.collectAsState()

    var showEuro by remember { mutableStateOf(false) }

    val totalSpent = expenses.sumOf { it.amountYen }
    val cashSpent = expenses.filter { it.paymentMethod == "Cash" }.sumOf { it.amountYen }
    val cardSpent = expenses.filter { it.paymentMethod == "Card" }.sumOf { it.amountYen }
    val totalWithdrawals = withdrawals.sumOf { it.amountYen }
    val cashRemaining = viewModel.getCashRemaining()

    val categoryTotals = expenses
        .groupBy { it.category }
        .mapValues { (_, values) -> values.sumOf { it.amountYen } }
        .toList()
        .sortedByDescending { it.second }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Statistics",
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

        StatCard("Total spent", formatAmount(totalSpent, showEuro))
        StatCard("Cash spent", formatAmount(cashSpent, showEuro))
        StatCard("Card spent", formatAmount(cardSpent, showEuro))
        StatCard("Total withdrawals", formatAmount(totalWithdrawals, showEuro))
        StatCard("Cash remaining", formatAmount(cashRemaining, showEuro))
        StatCard("Number of expenses", expenses.size.toString())

        if (categoryTotals.isNotEmpty()) {
            Text(
                text = "Top categories",
                style = MaterialTheme.typography.titleMedium
            )

            categoryTotals.take(5).forEach { (category, amount) ->
                StatCard(category, formatAmount(amount, showEuro))
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}