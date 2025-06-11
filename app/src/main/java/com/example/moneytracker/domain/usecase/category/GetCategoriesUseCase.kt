package com.example.moneytracker.domain.usecase.category

import com.example.moneytracker.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(type: String): Flow<List<com.example.moneytracker.data.local.entities.Category>> {
        return repository.getCategoriesByType(type)
    }
}
