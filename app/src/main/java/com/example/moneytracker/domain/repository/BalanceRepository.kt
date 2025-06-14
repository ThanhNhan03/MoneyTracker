package com.example.moneytracker.domain.repository

import com.example.moneytracker.data.local.entities.Balance
import kotlinx.coroutines.flow.Flow

interface BalanceRepository {
    fun getBalance(): Flow<Balance?>
    suspend fun hasBalance(): Boolean
    suspend fun updateBalance(balance: Balance)
} 