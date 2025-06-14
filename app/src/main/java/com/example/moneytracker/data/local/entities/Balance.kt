package com.example.moneytracker.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "balance")
data class Balance(
    @PrimaryKey
    val id: Int = 1,
    val amount: Double,
    val lastUpdated: Long = System.currentTimeMillis()
) 