package com.example.moneytracker.domain.usecase.category

import com.example.moneytracker.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCategoryByIdUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(id: Int): Flow<com.example.moneytracker.data.local.entities.Category?> {
        return repository.getCategoryById(id)
    }
}
