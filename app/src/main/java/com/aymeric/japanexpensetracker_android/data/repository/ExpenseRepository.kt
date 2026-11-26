package com.aymeric.japanexpensetracker_android.data.repository

import com.aymeric.japanexpensetracker_android.data.local.dao.ExpenseDao
import com.aymeric.japanexpensetracker_android.data.local.dao.WithdrawalDao
import com.aymeric.japanexpensetracker_android.data.local.entity.ExpenseEntity
import com.aymeric.japanexpensetracker_android.data.local.entity.WithdrawalEntity
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val withdrawalDao: WithdrawalDao
) {
    fun getAllExpenses(): Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()

    fun getAllWithdrawals(): Flow<List<WithdrawalEntity>> = withdrawalDao.getAllWithdrawals()

    fun getAllCategories(): Flow<List<String>> = expenseDao.getAllCategories()

    suspend fun getExpense(id: Int): ExpenseEntity? = expenseDao.getExpenseById(id)

    suspend fun insertExpense(expense: ExpenseEntity) = expenseDao.insertExpense(expense)

    /** Returns how many expenses were actually inserted (duplicates are skipped). */
    suspend fun importExpenses(expenses: List<ExpenseEntity>): Int =
        expenseDao.insertIgnoringDuplicates(expenses).count { it != -1L }

    suspend fun updateExpense(expense: ExpenseEntity) = expenseDao.updateExpense(expense)

    suspend fun deleteExpense(expense: ExpenseEntity) = expenseDao.deleteExpense(expense)

    suspend fun insertWithdrawal(withdrawal: WithdrawalEntity) =
        withdrawalDao.insertWithdrawal(withdrawal)

    suspend fun deleteAll() {
        expenseDao.deleteAllExpenses()
        withdrawalDao.deleteAllWithdrawals()
    }
}
