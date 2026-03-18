package com.aymeric.japanexpensetracker_android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aymeric.japanexpensetracker_android.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses ORDER BY dateMillis DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT DISTINCT category FROM expenses ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>

    @Query("SELECT * FROM expenses WHERE paymentMethod = :paymentMethod ORDER BY dateMillis DESC")
    fun getExpensesByPaymentMethod(paymentMethod: String): Flow<List<ExpenseEntity>>

    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()
}