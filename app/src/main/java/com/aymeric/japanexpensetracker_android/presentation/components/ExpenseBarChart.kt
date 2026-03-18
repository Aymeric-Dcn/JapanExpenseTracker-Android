package com.aymeric.japanexpensetracker_android.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aymeric.japanexpensetracker_android.data.local.entity.ExpenseEntity
import com.aymeric.japanexpensetracker_android.utils.formatAmount
import kotlin.math.max

@Composable
fun ExpenseBarChart(
    expenses: List<ExpenseEntity>,
    showEuro: Boolean,
    modifier: Modifier = Modifier
) {
    val maxAmount = max(expenses.maxOfOrNull { it.amountYen } ?: 1.0, 1.0)
    val maxHeight = 140.dp

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Expense chart",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            expenses.forEach { expense ->
                val ratio = (expense.amountYen / maxAmount).toFloat().coerceIn(0f, 1f)
                val barColor = if (expense.paymentMethod == "Cash") {
                    Color(0xFF2E7D32)
                } else {
                    Color(0xFF6A1B9A)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = formatAmount(expense.amountYen, showEuro),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .width(26.dp)
                            .height(maxHeight * ratio)
                            .background(
                                color = barColor,
                                shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = expense.category.take(6),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}