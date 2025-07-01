package com.example.moneytracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.domain.usecase.transaction.*
import com.example.moneytracker.domain.repository.CategoryRepository
import com.example.moneytracker.data.local.entities.Transaction
import com.example.moneytracker.data.local.entities.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _allTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val allTransactions: StateFlow<List<Transaction>> = _allTransactions

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _transactionToEdit = MutableStateFlow<Transaction?>(null)
    val transactionToEdit: StateFlow<Transaction?> = _transactionToEdit

    init {
        loadAllCategoriesInit()
    }

    fun loadAllCategories() {
        viewModelScope.launch {
            try {
                categoryRepository.getCategoriesByType("expense").collectLatest { expenseCategories ->
                    categoryRepository.getCategoriesByType("income").first().let { incomeCategories ->
                        _categories.value = expenseCategories + incomeCategories
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    private fun loadAllCategoriesInit() {
        viewModelScope.launch {
            try {
                categoryRepository.getCategoriesByType("expense").collectLatest { expenseCategories ->
                    categoryRepository.getCategoriesByType("income").first().let { incomeCategories ->
                        _categories.value = expenseCategories + incomeCategories
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun getCategoryName(categoryId: Int): String {
        return _categories.value.find { it.id == categoryId }?.name ?: "Category $categoryId"
    }

    fun loadTransactions(startDate: Date, endDate: Date) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getTransactionsUseCase(startDate, endDate).collectLatest { transactions ->
                    _transactions.value = transactions
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun loadAllTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load all transactions (from earliest date to now)
                val earliestDate = Date(0) // January 1, 1970
                val currentDate = Date()
                
                getTransactionsUseCase(earliestDate, currentDate).collectLatest { transactions ->
                    _allTransactions.value = transactions
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun loadTransaction(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Assuming you have a use case to get a single transaction by ID
                // If not, you'll need to implement GetTransactionUseCase
                getTransactionsUseCase(Date(0), Date()).collectLatest { transactions ->
                    val transaction = transactions.firstOrNull { it.id == id }
                _transactionToEdit.value = transaction
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                addTransactionUseCase(transaction)
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }
    
    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Update transaction in database
                updateTransactionUseCase(transaction)
                
                // Update local state
                _transactions.value = _transactions.value.map { 
                    if (it.id == transaction.id) transaction else it 
                }
                
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }
    
    fun setError(message: String) {
        _error.value = message
    }
    
    fun clearError() {
        _error.value = null
    }
}
