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

    suspend fun insertExpense(expense: ExpenseEntity) = expenseDao.insertExpense(expense)

    suspend fun insertWithdrawal(withdrawal: WithdrawalEntity) =
        withdrawalDao.insertWithdrawal(withdrawal)
}