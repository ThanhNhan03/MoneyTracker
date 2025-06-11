package com.example.moneytracker.data.local.dao

import androidx.room.*
import com.example.moneytracker.data.local.entities.Transaction
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE type = :type AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsByTypeAndDateRange(type: String, startDate: Date, endDate: Date): Flow<List<Transaction>>

    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)
    
    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type AND date BETWEEN :startDate AND :endDate")
    fun getTotalAmountByTypeAndDateRange(type: String, startDate: Date, endDate: Date): Flow<Double?>
}
