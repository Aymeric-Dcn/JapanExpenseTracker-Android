package com.aymeric.japanexpensetracker_android.presentation.screens.expenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aymeric.japanexpensetracker_android.data.local.entity.ExpenseEntity
import com.aymeric.japanexpensetracker_android.presentation.components.ExpenseBarChart
import com.aymeric.japanexpensetracker_android.presentation.viewmodel.ExpenseViewModel
import com.aymeric.japanexpensetracker_android.utils.PeriodFilter
import com.aymeric.japanexpensetracker_android.utils.formatAmount
import com.aymeric.japanexpensetracker_android.utils.isInSelectedPeriod
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    navController: NavController,
    viewModel: ExpenseViewModel
) {
    val expenses by viewModel.expenses.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var selectedPeriod by remember { mutableStateOf(PeriodFilter.DAY) }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedPayment by remember { mutableStateOf("All") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var paymentExpanded by remember { mutableStateOf(false) }
    var showEuro by remember { mutableStateOf(false) }

    val paymentOptions = listOf("All", "Cash", "Card")
    val categoryOptions = listOf("All") + categories

    val filteredExpenses = expenses.filter { expense ->
        val periodMatch = isInSelectedPeriod(expense.dateMillis, selectedPeriod)
        val categoryMatch = selectedCategory == "All" || expense.category == selectedCategory
        val paymentMatch = selectedPayment == "All" || expense.paymentMethod == selectedPayment
        periodMatch && categoryMatch && paymentMatch
    }

    val total = filteredExpenses.sumOf { it.amountYen }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Expenses",
            style = MaterialTheme.typography.headlineMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedPeriod == PeriodFilter.DAY,
                onClick = { selectedPeriod = PeriodFilter.DAY },
                label = { Text("Day") }
            )
            FilterChip(
                selected = selectedPeriod == PeriodFilter.WEEK,
                onClick = { selectedPeriod = PeriodFilter.WEEK },
                label = { Text("Week") }
            )
            FilterChip(
                selected = selectedPeriod == PeriodFilter.MONTH,
                onClick = { selectedPeriod = PeriodFilter.MONTH },
                label = { Text("Month") }
            )
            FilterChip(
                selected = selectedPeriod == PeriodFilter.TOTAL,
                onClick = { selectedPeriod = PeriodFilter.TOTAL },
                label = { Text("Total") }
            )
        }

        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                }
            )

            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                categoryOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedCategory = option
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        ExposedDropdownMenuBox(
            expanded = paymentExpanded,
            onExpandedChange = { paymentExpanded = !paymentExpanded }
        ) {
            OutlinedTextField(
                value = selectedPayment,
                onValueChange = {},
                readOnly = true,
                label = { Text("Payment") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = paymentExpanded)
                }
            )

            ExposedDropdownMenu(
                expanded = paymentExpanded,
                onDismissRequest = { paymentExpanded = false }
            ) {
                paymentOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedPayment = option
                            paymentExpanded = false
                        }
                    )
                }
            }
        }

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
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Selected total",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = formatAmount(total, showEuro),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        if (filteredExpenses.isNotEmpty()) {
            ExpenseBarChart(
                expenses = filteredExpenses.take(12),
                showEuro = showEuro,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1f, fill = false))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredExpenses) { expense ->
                ExpenseItem(
                    expense = expense,
                    showEuro = showEuro
                )
            }
        }
    }
}

@Composable
private fun ExpenseItem(
    expense: ExpenseEntity,
    showEuro: Boolean
) {
    val formatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = expense.category,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = formatAmount(expense.amountYen, showEuro),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Payment: ${expense.paymentMethod}",
                style = MaterialTheme.typography.bodyMedium
            )
            if (expense.note.isNotBlank()) {
                Text(
                    text = "Note: ${expense.note}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = formatter.format(Date(expense.dateMillis)),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}