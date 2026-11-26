package com.aymeric.japanexpensetracker_android.presentation.screens.expenses

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aymeric.japanexpensetracker_android.data.local.entity.ExpenseEntity
import com.aymeric.japanexpensetracker_android.domain.model.PaymentMethod
import com.aymeric.japanexpensetracker_android.domain.usecase.GetFilteredExpensesUseCase
import com.aymeric.japanexpensetracker_android.presentation.components.CurrencyToggle
import com.aymeric.japanexpensetracker_android.presentation.components.ExpenseBarChart
import com.aymeric.japanexpensetracker_android.presentation.navigation.Routes
import com.aymeric.japanexpensetracker_android.presentation.viewmodel.ExpenseViewModel
import com.aymeric.japanexpensetracker_android.utils.PeriodFilter
import com.aymeric.japanexpensetracker_android.utils.formatAmount
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val ALL = "All"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    navController: NavController,
    viewModel: ExpenseViewModel
) {
    val expenses by viewModel.expenses.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val showEuro by viewModel.showEuro.collectAsState()
    val rate by viewModel.exchangeRate.collectAsState()
    val context = LocalContext.current

    var selectedPeriod by remember { mutableStateOf(PeriodFilter.DAY) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedPayment by remember { mutableStateOf<PaymentMethod?>(null) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var paymentExpanded by remember { mutableStateOf(false) }

    val filterExpenses = remember { GetFilteredExpensesUseCase() }
    val filteredExpenses = filterExpenses(
        expenses = expenses,
        period = selectedPeriod,
        category = selectedCategory,
        paymentMethod = selectedPayment
    )
    val total = filteredExpenses.sumOf { it.amountYen }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            viewModel.exportCsv(uri, filteredExpenses) { ok ->
                val message = if (ok) "Exported ${filteredExpenses.size} expenses" else "Export failed"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Expenses",
                style = MaterialTheme.typography.headlineMedium
            )
            TextButton(
                onClick = { exportLauncher.launch("expenses-${selectedPeriod.name.lowercase()}.csv") },
                enabled = filteredExpenses.isNotEmpty()
            ) {
                Text("Export CSV")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PeriodFilter.entries.forEach { period ->
                FilterChip(
                    selected = selectedPeriod == period,
                    onClick = { selectedPeriod = period },
                    label = { Text(period.name.lowercase().replaceFirstChar { it.uppercase() }) }
                )
            }
        }

        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            OutlinedTextField(
                value = selectedCategory ?: ALL,
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
                (listOf<String?>(null) + categories).forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option ?: ALL) },
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
                value = selectedPayment?.label ?: ALL,
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
                (listOf<PaymentMethod?>(null) + PaymentMethod.entries).forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option?.label ?: ALL) },
                        onClick = {
                            selectedPayment = option
                            paymentExpanded = false
                        }
                    )
                }
            }
        }

        CurrencyToggle(
            showEuro = showEuro,
            onShowEuroChange = viewModel::setShowEuro,
            rate = rate
        )

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
                    text = formatAmount(total, showEuro, rate.yenToEur),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        if (filteredExpenses.isNotEmpty()) {
            ExpenseBarChart(
                expenses = filteredExpenses.take(12),
                showEuro = showEuro,
                yenToEur = rate.yenToEur,
                modifier = Modifier.fillMaxWidth()
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredExpenses, key = { it.id }) { expense ->
                ExpenseItem(
                    expense = expense,
                    showEuro = showEuro,
                    yenToEur = rate.yenToEur,
                    onClick = { navController.navigate(Routes.EditExpense.createRoute(expense.id)) }
                )
            }
        }
    }
}

@Composable
private fun ExpenseItem(
    expense: ExpenseEntity,
    showEuro: Boolean,
    yenToEur: Double,
    onClick: () -> Unit
) {
    val formatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = expense.category,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = formatAmount(expense.amountYen, showEuro, yenToEur),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Payment: ${expense.paymentMethod.label}",
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
