package com.aymeric.japanexpensetracker_android.presentation.screens.addwithdrawal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aymeric.japanexpensetracker_android.presentation.viewmodel.ExpenseViewModel

@Composable
fun AddWithdrawalScreen(
    navController: NavController,
    viewModel: ExpenseViewModel
) {
    var amount by remember { mutableStateOf("") }

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
            text = "Current cash remaining: %.2f ¥".format(viewModel.getCashRemaining()),
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount withdrawn in yen") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                val amountValue = amount.toDoubleOrNull()
                if (amountValue != null) {
                    viewModel.addWithdrawal(
                        amountYen = amountValue,
                        dateMillis = System.currentTimeMillis()
                    )
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Withdrawal")
        }
    }
}