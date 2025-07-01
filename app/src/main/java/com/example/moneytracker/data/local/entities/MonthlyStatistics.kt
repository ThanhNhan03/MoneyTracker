package com.example.moneytracker.data.local.entities

data class MonthlyStatistics(
    val month: String,
    val income: Double,
    val expense: Double
) {
    val balance: Double
        get() = income - expense
}
