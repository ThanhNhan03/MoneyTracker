package com.example.moneytracker.data.repository

import com.example.moneytracker.data.local.dao.TransactionDao
import com.example.moneytracker.data.local.entities.Transaction
import com.example.moneytracker.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {
    override fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByDateRange(startDate, endDate)
    }

    override fun getTransactionsByTypeAndDateRange(
        type: String,
        startDate: Date,
        endDate: Date
    ): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByTypeAndDateRange(type, startDate, endDate)
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction)
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }

    override fun getTotalAmountByTypeAndDateRange(
        type: String,
        startDate: Date,
        endDate: Date
    ): Flow<Double?> {
        return transactionDao.getTotalAmountByTypeAndDateRange(type, startDate, endDate)
    }
}
