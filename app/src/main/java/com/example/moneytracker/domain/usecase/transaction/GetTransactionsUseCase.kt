package com.example.moneytracker.domain.usecase.transaction

import com.example.moneytracker.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(startDate: Date, endDate: Date): Flow<List<com.example.moneytracker.data.local.entities.Transaction>> {
        return repository.getTransactionsByDateRange(startDate, endDate)
    }
}
