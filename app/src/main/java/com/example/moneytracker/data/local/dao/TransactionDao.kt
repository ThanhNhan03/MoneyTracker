package com.example.moneytracker.data.local.dao

import androidx.room.*
import com.example.moneytracker.data.local.entities.Transaction
import com.example.moneytracker.data.local.entities.MonthlyStatistics
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
    
    @Query("""
        SELECT 
            strftime('%Y-%m', date/1000, 'unixepoch') as month,
            SUM(CASE WHEN type = 'income' THEN amount ELSE 0 END) as income,
            SUM(CASE WHEN type = 'expense' THEN amount ELSE 0 END) as expense
        FROM transactions 
        WHERE date BETWEEN :startDate AND :endDate
        GROUP BY strftime('%Y-%m', date/1000, 'unixepoch')
        ORDER BY month
    """)
    suspend fun getMonthlyStatistics(startDate: Date, endDate: Date): List<MonthlyStatistics>
}
