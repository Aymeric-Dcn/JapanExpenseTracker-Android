package com.aymeric.japanexpensetracker_android.presentation.viewmodel

import android.app.Application
import android.net.Uri
import android.nfc.Tag
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aymeric.felica.SuicaCard
import com.aymeric.japanexpensetracker_android.data.local.database.AppDatabase
import com.aymeric.japanexpensetracker_android.data.local.entity.ExpenseEntity
import com.aymeric.japanexpensetracker_android.data.local.entity.WithdrawalEntity
import com.aymeric.japanexpensetracker_android.data.nfc.SuicaReader
import com.aymeric.japanexpensetracker_android.data.repository.ExchangeRateRepository
import com.aymeric.japanexpensetracker_android.data.repository.ExpenseRepository
import com.aymeric.japanexpensetracker_android.domain.model.ExchangeRate
import com.aymeric.japanexpensetracker_android.domain.model.PaymentMethod
import com.aymeric.japanexpensetracker_android.domain.usecase.GetCashRemainingUseCase
import com.aymeric.japanexpensetracker_android.domain.usecase.ImportIcCardHistoryUseCase
import com.aymeric.japanexpensetracker_android.utils.CsvExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface ScanState {
    data object Waiting : ScanState
    data object Reading : ScanState
    data class Success(val card: SuicaCard, val importedCount: Int? = null) : ScanState
    data class Error(val message: String) : ScanState
}

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.build(application)

    private val repository = ExpenseRepository(
        expenseDao = database.expenseDao(),
        withdrawalDao = database.withdrawalDao()
    )
    private val exchangeRateRepository = ExchangeRateRepository(application)
    private val suicaReader = SuicaReader()

    private val getCashRemainingUseCase = GetCashRemainingUseCase()
    private val importIcCardHistory = ImportIcCardHistoryUseCase()

    private val _expenses = MutableStateFlow<List<ExpenseEntity>>(emptyList())
    val expenses: StateFlow<List<ExpenseEntity>> = _expenses.asStateFlow()

    private val _withdrawals = MutableStateFlow<List<WithdrawalEntity>>(emptyList())
    val withdrawals: StateFlow<List<WithdrawalEntity>> = _withdrawals.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _exchangeRate = MutableStateFlow(
        exchangeRateRepository.cached() ?: ExchangeRate.FALLBACK
    )
    val exchangeRate: StateFlow<ExchangeRate> = _exchangeRate.asStateFlow()

    /** Shared by every screen so the yen / euro choice stays the same everywhere. */
    private val _showEuro = MutableStateFlow(false)
    val showEuro: StateFlow<Boolean> = _showEuro.asStateFlow()

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Waiting)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    init {
        viewModelScope.launch { repository.getAllExpenses().collect { _expenses.value = it } }
        viewModelScope.launch { repository.getAllWithdrawals().collect { _withdrawals.value = it } }
        viewModelScope.launch { repository.getAllCategories().collect { _categories.value = it } }
        refreshExchangeRate()
    }

    fun setShowEuro(show: Boolean) {
        _showEuro.value = show
    }

    fun refreshExchangeRate() {
        viewModelScope.launch { _exchangeRate.value = exchangeRateRepository.refresh() }
    }

    // --- Expenses ---------------------------------------------------------

    suspend fun getExpense(id: Int): ExpenseEntity? = repository.getExpense(id)

    fun addExpense(
        amountYen: Long,
        paymentMethod: PaymentMethod,
        category: String,
        note: String,
        dateMillis: Long
    ) {
        viewModelScope.launch {
            repository.insertExpense(
                ExpenseEntity(
                    amountYen = amountYen,
                    paymentMethod = paymentMethod,
                    category = category,
                    note = note,
                    dateMillis = dateMillis
                )
            )
        }
    }

    fun updateExpense(expense: ExpenseEntity) {
        viewModelScope.launch { repository.updateExpense(expense) }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch { repository.deleteExpense(expense) }
    }

    fun addWithdrawal(amountYen: Long, dateMillis: Long) {
        viewModelScope.launch {
            repository.insertWithdrawal(WithdrawalEntity(amountYen = amountYen, dateMillis = dateMillis))
        }
    }

    fun getCashRemaining(): Long = getCashRemainingUseCase(
        expenses = expenses.value,
        withdrawals = withdrawals.value
    )

    /** Writes [expenses] as CSV to a document the user picked with the system file picker. */
    fun exportCsv(uri: Uri, expenses: List<ExpenseEntity>, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            val ok = withContext(Dispatchers.IO) {
                runCatching {
                    val csv = CsvExporter.toCsv(expenses)
                    getApplication<Application>().contentResolver.openOutputStream(uri)?.use {
                        it.write(csv.toByteArray(Charsets.UTF_8))
                    } ?: error("Cannot open $uri")
                }.isSuccess
            }
            onDone(ok)
        }
    }

    // --- IC card ----------------------------------------------------------

    fun resetScan() {
        _scanState.value = ScanState.Waiting
    }

    /** Called by the NFC reader mode callback, on a binder thread. */
    fun onTagDiscovered(tag: Tag) {
        _scanState.value = ScanState.Reading
        viewModelScope.launch(Dispatchers.IO) {
            _scanState.value = try {
                ScanState.Success(suicaReader.read(tag))
            } catch (e: Exception) {
                ScanState.Error(e.message ?: "Could not read the card, hold it still and retry")
            }
        }
    }

    fun importScannedCard() {
        val state = _scanState.value as? ScanState.Success ?: return
        viewModelScope.launch {
            val inserted = repository.importExpenses(importIcCardHistory(state.card))
            _scanState.value = state.copy(importedCount = inserted)
        }
    }
}
