package com.example.moneytracker.domain.usecase.balance

import com.example.moneytracker.domain.repository.BalanceRepository
import javax.inject.Inject

class UpdateBalanceUseCase @Inject constructor(
    private val repository: BalanceRepository
) {
    suspend operator fun invoke(balance: com.example.moneytracker.data.local.entities.Balance) {
        repository.updateBalance(balance)
    }
} 