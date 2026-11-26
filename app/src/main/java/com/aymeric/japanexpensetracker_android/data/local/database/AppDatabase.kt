package com.aymeric.japanexpensetracker_android.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aymeric.japanexpensetracker_android.data.local.dao.ExpenseDao
import com.aymeric.japanexpensetracker_android.data.local.dao.WithdrawalDao
import com.aymeric.japanexpensetracker_android.data.local.entity.ExpenseEntity
import com.aymeric.japanexpensetracker_android.data.local.entity.WithdrawalEntity

@Database(
    entities = [ExpenseEntity::class, WithdrawalEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun withdrawalDao(): WithdrawalDao

    companion object {
        private const val DATABASE_NAME = "expense_tracker_db"

        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .addMigrations(MIGRATION_1_2)
                .build()
    }
}
