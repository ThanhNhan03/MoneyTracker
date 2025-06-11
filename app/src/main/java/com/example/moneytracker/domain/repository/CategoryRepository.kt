package com.example.moneytracker.domain.repository

import com.example.moneytracker.data.local.entities.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategoriesByType(type: String): Flow<List<Category>>
    suspend fun insertCategory(category: Category)
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(category: Category)
}
