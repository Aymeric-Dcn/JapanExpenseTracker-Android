package com.aymeric.japanexpensetracker_android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aymeric.japanexpensetracker_android.data.local.entity.WithdrawalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WithdrawalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawal(withdrawal: WithdrawalEntity)

    @Query("SELECT * FROM withdrawals ORDER BY dateMillis DESC")
    fun getAllWithdrawals(): Flow<List<WithdrawalEntity>>

    @Query("DELETE FROM withdrawals")
    suspend fun deleteAllWithdrawals()
}