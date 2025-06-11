package com.example.moneytracker.domain.usecase.category

import com.example.moneytracker.domain.repository.CategoryRepository
import javax.inject.Inject

class AddCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: com.example.moneytracker.data.local.entities.Category) {
        repository.insertCategory(category)
    }
}
