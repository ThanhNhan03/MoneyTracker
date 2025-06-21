package com.example.moneytracker.domain.usecase.transaction

import com.example.moneytracker.domain.repository.TransactionRepository
import javax.inject.Inject

class UpdateTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: com.example.moneytracker.data.local.entities.Transaction) {
        repository.updateTransaction(transaction)
    }
}
