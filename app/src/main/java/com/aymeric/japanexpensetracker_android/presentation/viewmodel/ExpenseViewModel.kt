package com.aymeric.japanexpensetracker_android.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.aymeric.japanexpensetracker_android.data.local.database.AppDatabase
import com.aymeric.japanexpensetracker_android.data.local.entity.ExpenseEntity
import com.aymeric.japanexpensetracker_android.data.local.entity.WithdrawalEntity
import com.aymeric.japanexpensetracker_android.data.repository.ExpenseRepository
import com.aymeric.japanexpensetracker_android.domain.usecase.GetCashRemainingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val database = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "expense_tracker_db"
    ).build()

    private val repository = ExpenseRepository(
        expenseDao = database.expenseDao(),
        withdrawalDao = database.withdrawalDao()
    )

    private val getCashRemainingUseCase = GetCashRemainingUseCase()

    private val _expenses = MutableStateFlow<List<ExpenseEntity>>(emptyList())
    val expenses: StateFlow<List<ExpenseEntity>> = _expenses.asStateFlow()

    private val _withdrawals = MutableStateFlow<List<WithdrawalEntity>>(emptyList())
    val withdrawals: StateFlow<List<WithdrawalEntity>> = _withdrawals.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    init {
        observeExpenses()
        observeWithdrawals()
        observeCategories()
    }

    private fun observeExpenses() {
        viewModelScope.launch {
            repository.getAllExpenses().collect { expenseList ->
                _expenses.value = expenseList
            }
        }
    }

    private fun observeWithdrawals() {
        viewModelScope.launch {
            repository.getAllWithdrawals().collect { withdrawalList ->
                _withdrawals.value = withdrawalList
            }
        }
    }

    private fun observeCategories() {
        viewModelScope.launch {
            repository.getAllCategories().collect { categoryList ->
                _categories.value = categoryList
            }
        }
    }

    fun addExpense(
        amountYen: Double,
        paymentMethod: String,
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

    fun addWithdrawal(
        amountYen: Double,
        dateMillis: Long
    ) {
        viewModelScope.launch {
            repository.insertWithdrawal(
                WithdrawalEntity(
                    amountYen = amountYen,
                    dateMillis = dateMillis
                )
            )
        }
    }

    fun getTotalSpent(): Double {
        return expenses.value.sumOf { it.amountYen }
    }

    fun getCashRemaining(): Double {
        return getCashRemainingUseCase(
            expenses = expenses.value,
            withdrawals = withdrawals.value
        )
    }
}