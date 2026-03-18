package com.aymeric.japanexpensetracker_android.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import com.aymeric.japanexpensetracker_android.utils.formatAmount

@Composable
fun CategoryPieChart(
    data: List<Pair<String, Double>>,
    showEuro: Boolean,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        Text(
            text = "No category data yet",
            style = MaterialTheme.typography.bodyMedium
        )
        return
    }

    val total = data.sumOf { it.second }.takeIf { it > 0 } ?: 1.0

    val colors = listOf(
        Color(0xFF2E7D32),
        Color(0xFF6A1B9A),
        Color(0xFF1565C0),
        Color(0xFFEF6C00),
        Color(0xFFC62828),
        Color(0xFF00838F),
        Color(0xFFAD1457),
        Color(0xFF5D4037)
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Spending by category",
            style = MaterialTheme.typography.titleMedium
        )

        Canvas(
            modifier = Modifier.size(220.dp)
        ) {
            var startAngle = -90f

            data.forEachIndexed { index, (_, amount) ->
                val sweepAngle = ((amount / total) * 360f).toFloat()
                drawArc(
                    color = colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    size = Size(size.width, size.height)
                )
                startAngle += sweepAngle
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            data.forEachIndexed { index, (category, amount) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Spacer(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    color = colors[index % colors.size],
                                    shape = CircleShape
                                )
                        )
                        Text(
                            text = category,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Text(
                        text = formatAmount(amount, showEuro),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}