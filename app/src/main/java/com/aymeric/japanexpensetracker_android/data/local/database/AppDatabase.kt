package com.aymeric.japanexpensetracker_android.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aymeric.japanexpensetracker_android.data.local.dao.ExpenseDao
import com.aymeric.japanexpensetracker_android.data.local.dao.WithdrawalDao
import com.aymeric.japanexpensetracker_android.data.local.entity.ExpenseEntity
import com.aymeric.japanexpensetracker_android.data.local.entity.WithdrawalEntity

@Database(
    entities = [ExpenseEntity::class, WithdrawalEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun withdrawalDao(): WithdrawalDao
}