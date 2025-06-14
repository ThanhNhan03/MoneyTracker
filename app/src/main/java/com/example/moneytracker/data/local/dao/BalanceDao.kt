package com.example.moneytracker.data.local.dao

import androidx.room.*
import com.example.moneytracker.data.local.entities.Balance
import kotlinx.coroutines.flow.Flow

@Dao
interface BalanceDao {
    @Query("SELECT * FROM balance WHERE id = 1")
    fun getBalance(): Flow<Balance?>

    @Query("SELECT COUNT(*) FROM balance WHERE id = 1")
    suspend fun hasBalance(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBalance(balance: Balance)

    @Update
    suspend fun updateBalance(balance: Balance)
} 