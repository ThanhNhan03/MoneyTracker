package com.example.moneytracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.domain.usecase.transaction.*
import com.example.moneytracker.data.local.entities.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val addTransactionUseCase: AddTransactionUseCase
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _transactionToEdit = MutableStateFlow<Transaction?>(null)
    val transactionToEdit: StateFlow<Transaction?> = _transactionToEdit

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

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
                addTransactionUseCase(transaction)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    
    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                // Assuming you have an update use case
                // If not, you'll need to implement UpdateTransactionUseCase
                // updateTransactionUseCase(transaction)
                
                // For now, just update the local state
                _transactions.value = _transactions.value.map { 
                    if (it.id == transaction.id) transaction else it 
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}
