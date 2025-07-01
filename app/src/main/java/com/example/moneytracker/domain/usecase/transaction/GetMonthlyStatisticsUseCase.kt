package com.example.moneytracker.domain.usecase.transaction

import com.example.moneytracker.data.local.entities.MonthlyStatistics
import com.example.moneytracker.domain.repository.TransactionRepository
import java.util.*
import javax.inject.Inject

class GetMonthlyStatisticsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(startDate: Date, endDate: Date): List<MonthlyStatistics> {
        return repository.getMonthlyStatistics(startDate, endDate)
    }
}
