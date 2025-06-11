package com.example.moneytracker.domain.usecase.transaction

import com.example.moneytracker.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: com.example.moneytracker.data.local.entities.Transaction) {
        repository.insertTransaction(transaction)
    }
}
