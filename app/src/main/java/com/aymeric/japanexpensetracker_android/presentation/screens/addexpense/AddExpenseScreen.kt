package com.aymeric.japanexpensetracker_android.presentation.screens.addexpense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    navController: NavController,
    viewModel: ExpenseViewModel
) {
    val categories by viewModel.categories.collectAsState()

    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Cash") }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Add Expense",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount in yen") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
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
                singleLine = true
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
            RowOption(
                label = "Cash",
                selected = paymentMethod == "Cash",
                onClick = { paymentMethod = "Cash" }
            )
            RowOption(
                label = "Card",
                selected = paymentMethod == "Card",
                onClick = { paymentMethod = "Card" }
            )
        }

        Button(
            onClick = {
                val amountValue = amount.toDoubleOrNull()
                if (amountValue != null && category.isNotBlank()) {
                    viewModel.addExpense(
                        amountYen = amountValue,
                        paymentMethod = paymentMethod,
                        category = category.trim(),
                        note = note.trim(),
                        dateMillis = System.currentTimeMillis()
                    )
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Expense")
        }
    }
}

@Composable
private fun RowOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(
            text = label,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}