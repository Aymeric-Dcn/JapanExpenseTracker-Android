package com.aymeric.japanexpensetracker_android.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.aymeric.japanexpensetracker_android.domain.model.ExchangeRate
import java.util.Locale

@Composable
fun CurrencyToggle(
    showEuro: Boolean,
    onShowEuroChange: (Boolean) -> Unit,
    rate: ExchangeRate,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Show in euro")
            Text(
                text = rateDescription(rate),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = showEuro, onCheckedChange = onShowEuroChange)
    }
}

private fun rateDescription(rate: ExchangeRate): String {
    val eurPer1000Yen = String.format(Locale.US, "%.2f", rate.yenToEur * 1000)
    val origin = when (rate.source) {
        ExchangeRate.Source.LIVE -> "ECB rate of ${rate.date}"
        ExchangeRate.Source.CACHED -> "offline, ECB rate of ${rate.date}"
        ExchangeRate.Source.FALLBACK -> "offline, approximate rate"
    }
    return "¥1,000 = $eurPer1000Yen € ($origin)"
}
