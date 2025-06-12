package com.example.moneytracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.data.local.DefaultCategories
import com.example.moneytracker.data.local.entities.Category
import com.example.moneytracker.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: CategoryRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _categoryToEdit = MutableStateFlow<Category?>(null)
    val categoryToEdit: StateFlow<Category?> = _categoryToEdit.asStateFlow()

    private val _showDeleteConfirmation = MutableStateFlow<Category?>(null)
    val showDeleteConfirmation: StateFlow<Category?> = _showDeleteConfirmation.asStateFlow()

    init {
        // Add default categories if database is empty
        viewModelScope.launch {
            repository.getCategoriesByType("expense").first().let { categories ->
                if (categories.isEmpty()) {
                    DefaultCategories.allCategories.forEach { category ->
                        repository.insertCategory(category)
                    }
                }
            }
        }
    }

    fun loadCategories(type: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.getCategoriesByType(type)
                    .collect { categories ->
                    _categories.value = categories
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun loadCategory(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.getCategoriesByType("expense").first().find { it.id == id }?.let {
                    _categoryToEdit.value = it
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun addCategory(category: Category) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Check if category name already exists
                val existingCategories = repository.getCategoriesByType(category.type).first()
                if (existingCategories.any { it.name.equals(category.name, ignoreCase = true) }) {
                    _error.value = "error_category_exists"
                    _isLoading.value = false
                    return@launch
                }
                repository.insertCategory(category)
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun updateCategory(id: Int, name: String, type: String, icon: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Check if category name already exists (excluding current category)
                val existingCategories = repository.getCategoriesByType(type).first()
                if (existingCategories.any { it.name.equals(name, ignoreCase = true) && it.id != id }) {
                    _error.value = "error_category_exists"
                    _isLoading.value = false
                    return@launch
                }
                val updatedCategory = Category(
                    id = id,
                    name = name,
                    type = type,
                    icon = icon
                )
                repository.updateCategory(updatedCategory)
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun showDeleteConfirmation(category: Category) {
        if (!category.isDefault) {
            _showDeleteConfirmation.value = category
        }
    }

    fun hideDeleteConfirmation() {
        _showDeleteConfirmation.value = null
    }

    fun deleteCategory(category: Category) {
        if (!category.isDefault) {
            viewModelScope.launch {
                _isLoading.value = true
                _error.value = null
                try {
                    repository.deleteCategory(category)
                    _isLoading.value = false
                } catch (e: Exception) {
                    _error.value = e.message
                    _isLoading.value = false
                }
            }
        }
    }
}
