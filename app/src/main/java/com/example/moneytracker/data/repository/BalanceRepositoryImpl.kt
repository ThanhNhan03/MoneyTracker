package com.example.moneytracker.data.repository

import com.example.moneytracker.data.local.dao.BalanceDao
import com.example.moneytracker.data.local.entities.Balance
import com.example.moneytracker.domain.repository.BalanceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BalanceRepositoryImpl @Inject constructor(
    private val balanceDao: BalanceDao
) : BalanceRepository {
    override fun getBalance(): Flow<Balance?> {
        return balanceDao.getBalance()
    }

    override suspend fun hasBalance(): Boolean {
        return balanceDao.hasBalance() > 0
    }

    override suspend fun updateBalance(balance: Balance) {
        balanceDao.insertBalance(balance)
    }
} 