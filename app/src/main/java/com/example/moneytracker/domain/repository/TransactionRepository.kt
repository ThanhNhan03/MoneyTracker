package com.example.moneytracker.domain.repository

import com.example.moneytracker.data.local.entities.Transaction
import kotlinx.coroutines.flow.Flow
import java.util.*

interface TransactionRepository {
    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<Transaction>>
    fun getTransactionsByTypeAndDateRange(type: String, startDate: Date, endDate: Date): Flow<List<Transaction>>
    suspend fun insertTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
    fun getTotalAmountByTypeAndDateRange(type: String, startDate: Date, endDate: Date): Flow<Double?>
}
