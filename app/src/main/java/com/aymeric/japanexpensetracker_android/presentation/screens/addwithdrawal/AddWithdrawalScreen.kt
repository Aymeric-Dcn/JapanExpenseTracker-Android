package com.aymeric.japanexpensetracker_android.presentation.screens.addwithdrawal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aymeric.japanexpensetracker_android.presentation.viewmodel.ExpenseViewModel
import com.aymeric.japanexpensetracker_android.utils.formatYen

@Composable
fun AddWithdrawalScreen(
    navController: NavController,
    viewModel: ExpenseViewModel
) {
    // Collected so the cash remaining below stays up to date.
    val expenses by viewModel.expenses.collectAsState()
    val withdrawals by viewModel.withdrawals.collectAsState()

    var amount by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    val amountValue = amount.trim().toLongOrNull()?.takeIf { it > 0 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Add Cash Withdrawal",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Current cash remaining: ${formatYen(viewModel.getCashRemaining())}",
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { input -> amount = input.filter { it.isDigit() } },
            label = { Text("Amount withdrawn in yen") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = showError && amountValue == null
        )

        Button(
            onClick = {
                if (amountValue == null) {
                    showError = true
                    return@Button
                }
                viewModel.addWithdrawal(
                    amountYen = amountValue,
                    dateMillis = System.currentTimeMillis()
                )
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Withdrawal")
        }
    }
}
