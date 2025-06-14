package com.example.moneytracker.domain.usecase.balance

import com.example.moneytracker.domain.repository.BalanceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBalanceUseCase @Inject constructor(
    private val repository: BalanceRepository
) {
    operator fun invoke(): Flow<com.example.moneytracker.data.local.entities.Balance?> {
        return repository.getBalance()
    }
} 