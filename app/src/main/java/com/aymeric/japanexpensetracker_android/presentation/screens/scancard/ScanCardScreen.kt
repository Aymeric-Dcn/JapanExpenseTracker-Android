package com.aymeric.japanexpensetracker_android.presentation.screens.scancard

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.nfc.NfcAdapter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aymeric.felica.SuicaCard
import com.aymeric.felica.SuicaHistoryEntry
import com.aymeric.japanexpensetracker_android.presentation.viewmodel.ExpenseViewModel
import com.aymeric.japanexpensetracker_android.presentation.viewmodel.ScanState
import com.aymeric.japanexpensetracker_android.utils.formatYen

/**
 * Reads a Suica / Pasmo / ICOCA card held against the phone and offers to import
 * its payments as expenses.
 *
 * Reader mode is used instead of an intent filter: the card is only read while this
 * screen is open, and Android does not show its own "new tag" screen.
 */
@Composable
fun ScanCardScreen(
    navController: NavController,
    viewModel: ExpenseViewModel
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val nfcAdapter = remember(context) { NfcAdapter.getDefaultAdapter(context) }
    val scanState by viewModel.scanState.collectAsState()

    DisposableEffect(activity, nfcAdapter) {
        viewModel.resetScan()
        if (activity != null && nfcAdapter != null && nfcAdapter.isEnabled) {
            nfcAdapter.enableReaderMode(
                activity,
                { tag -> viewModel.onTagDiscovered(tag) },
                NfcAdapter.FLAG_READER_NFC_F or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                null
            )
        }
        onDispose {
            if (activity != null) nfcAdapter?.disableReaderMode(activity)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Scan IC card",
            style = MaterialTheme.typography.headlineMedium
        )

        when {
            nfcAdapter == null -> Message("This phone has no NFC reader.")
            !nfcAdapter.isEnabled -> Message("NFC is turned off. Enable it in the system settings, then reopen this screen.")
            else -> when (val state = scanState) {
                ScanState.Waiting -> Message("Hold a Suica, Pasmo or ICOCA card against the back of the phone.")
                ScanState.Reading -> Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
                    Text("Reading the card, keep it still...")
                }
                is ScanState.Error -> {
                    Message(state.message)
                    OutlinedButton(onClick = viewModel::resetScan) { Text("Try again") }
                }
                is ScanState.Success -> CardResult(
                    card = state.card,
                    importedCount = state.importedCount,
                    onImport = viewModel::importScannedCard,
                    onDone = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun CardResult(
    card: SuicaCard,
    importedCount: Int?,
    onImport: () -> Unit,
    onDone: () -> Unit
) {
    val payments = card.history.count { it.isPayment }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Card balance", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = card.balanceYen?.let { formatYen(it.toLong()) } ?: "No history on this card",
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    text = "${card.history.size} transactions read, $payments payments",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (importedCount == null) {
            Button(
                onClick = onImport,
                enabled = payments > 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Import $payments payments as expenses")
            }
        } else {
            Message(
                if (importedCount == 0) "Nothing new: these payments were already imported."
                else "$importedCount new expenses imported."
            )
            Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) { Text("Done") }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(card.history) { entry -> HistoryRow(entry) }
        }
    }
}

@Composable
private fun HistoryRow(entry: SuicaHistoryEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(entry.processType.label, style = MaterialTheme.typography.titleSmall)
                Text(entry.date.toString(), style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = entry.amountYen?.let { amount ->
                        (if (amount > 0) "+" else "") + formatYen(amount.toLong())
                    } ?: "-",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Balance ${formatYen(entry.balanceYen.toLong())}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun Message(text: String) {
    Text(text, style = MaterialTheme.typography.bodyLarge)
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
