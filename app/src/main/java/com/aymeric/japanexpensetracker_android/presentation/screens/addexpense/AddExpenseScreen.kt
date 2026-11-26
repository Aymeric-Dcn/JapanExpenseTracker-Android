package com.aymeric.japanexpensetracker_android.presentation.screens.addexpense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aymeric.japanexpensetracker_android.data.local.entity.ExpenseEntity
import com.aymeric.japanexpensetracker_android.domain.model.PaymentMethod
import com.aymeric.japanexpensetracker_android.presentation.viewmodel.ExpenseViewModel

/**
 * Creates an expense, or edits and deletes an existing one when [expenseId] is set.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    navController: NavController,
    viewModel: ExpenseViewModel,
    expenseId: Int? = null
) {
    val categories by viewModel.categories.collectAsState()

    var existing by remember { mutableStateOf<ExpenseEntity?>(null) }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    var expanded by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }

    LaunchedEffect(expenseId) {
        if (expenseId != null) {
            viewModel.getExpense(expenseId)?.let { expense ->
                existing = expense
                amount = expense.amountYen.toString()
                category = expense.category
                note = expense.note
                paymentMethod = expense.paymentMethod
            }
        }
    }

    val isEditing = expenseId != null
    val amountValue = amount.trim().toLongOrNull()?.takeIf { it > 0 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (isEditing) "Edit Expense" else "Add Expense",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { input -> amount = input.filter { it.isDigit() } },
            label = { Text("Amount in yen") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = showError && amountValue == null,
            supportingText = {
                if (showError && amountValue == null) Text("Enter a whole amount in yen")
            }
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                singleLine = true,
                isError = showError && category.isBlank()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { existingCategory ->
                    DropdownMenuItem(
                        text = { Text(existingCategory) },
                        onClick = {
                            category = existingCategory
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Note") },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Payment Method",
            style = MaterialTheme.typography.titleMedium
        )

        Column {
            PaymentMethod.entries.forEach { method ->
                RowOption(
                    label = method.label,
                    selected = paymentMethod == method,
                    onClick = { paymentMethod = method }
                )
            }
        }

        Button(
            onClick = {
                if (amountValue == null || category.isBlank()) {
                    showError = true
                    return@Button
                }
                val current = existing
                if (current != null) {
                    viewModel.updateExpense(
                        current.copy(
                            amountYen = amountValue,
                            paymentMethod = paymentMethod,
                            category = category.trim(),
                            note = note.trim()
                        )
                    )
                } else {
                    viewModel.addExpense(
                        amountYen = amountValue,
                        paymentMethod = paymentMethod,
                        category = category.trim(),
                        note = note.trim(),
                        dateMillis = System.currentTimeMillis()
                    )
                }
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isEditing) "Save Changes" else "Save Expense")
        }

        if (existing != null) {
            OutlinedButton(
                onClick = { confirmDelete = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete Expense", color = MaterialTheme.colorScheme.error)
            }
        }
    }

    val toDelete = existing
    if (confirmDelete && toDelete != null) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Delete this expense?") },
            text = { Text("This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    viewModel.deleteExpense(toDelete)
                    navController.popBackStack()
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun RowOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(text = label)
    }
}
