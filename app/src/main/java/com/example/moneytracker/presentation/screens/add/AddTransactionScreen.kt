package com.example.moneytracker.presentation.screens.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Note
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneytracker.R
import com.example.moneytracker.data.local.entities.Transaction
import com.example.moneytracker.presentation.viewmodel.TransactionViewModel
import com.example.moneytracker.presentation.viewmodel.CategoryViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onBackClick: () -> Unit,
    transactionId: Int? = null,
    viewModel: TransactionViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    // Load transaction data if in edit mode
    LaunchedEffect(transactionId) {
        transactionId?.let { viewModel.loadTransaction(it) }
    }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("expense") }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    var selectedCategoryName by remember { mutableStateOf("") }
    var showCategoryDialog by remember { mutableStateOf(false) }
    
    // Load categories when type changes
    LaunchedEffect(selectedType) {
        categoryViewModel.loadCategories(selectedType)
    }
    
    // Observe states
    val categories by categoryViewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // Observe the transaction to edit
    val transactionToEdit by viewModel.transactionToEdit.collectAsState()
    
    // Update UI when transaction data is loaded
    LaunchedEffect(transactionToEdit) {
        transactionToEdit?.let { transaction ->
            amount = transaction.amount.toString()
            note = transaction.note ?: ""
            selectedType = transaction.type
            selectedCategoryId = transaction.categoryId
            // Find category name
            categories.find { it.id == transaction.categoryId }?.let {
                selectedCategoryName = it.name
            }
        }
    }
    
    // Update category name when categories change
    LaunchedEffect(categories, selectedCategoryId) {
        selectedCategoryId?.let { id ->
            categories.find { it.id == id }?.let {
                selectedCategoryName = it.name
            }
        }
    }
    
    // Show error message
    LaunchedEffect(error) {
        error?.let {
            // TODO: Show snackbar or toast with error message
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (transactionId != null) 
                            stringResource(R.string.edit_transaction)
                        else 
                            stringResource(R.string.add_transaction)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Transaction Type Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    "expense" to stringResource(R.string.expense),
                    "income" to stringResource(R.string.income)
                ).forEach { (type, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .selectable(
                                selected = (type == selectedType),
                                onClick = { selectedType = type }
                            )
                            .padding(8.dp)
                    ) {
                        RadioButton(
                            selected = (type == selectedType),
                            onClick = { selectedType = type }
                        )
                        Text(label)
                    }
                }
            }

            // Amount Input
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text(stringResource(R.string.amount)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default.copy(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = stringResource(R.string.amount)
                    )
                }
            )

            // Category Selection
            OutlinedButton(
                onClick = { showCategoryDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Category,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(selectedCategoryName.ifEmpty { stringResource(R.string.select_category) })
            }

            // Note Input
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(stringResource(R.string.note_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 3,
                maxLines = 3,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Note,
                        contentDescription = stringResource(R.string.note_hint)
                    )
                }
            )

            // Save Button
            Button(
                onClick = {
                    // Validate input
                    val amountValue = amount.toDoubleOrNull()
                    if (amountValue == null || amountValue <= 0) {
                        viewModel.setError("error_invalid_amount")
                        return@Button
                    }
                    if (selectedCategoryId == null) {
                        viewModel.setError("error_category_required")
                        return@Button
                    }
                    
                    // Create transaction
                    val transaction = Transaction(
                        id = transactionId ?: 0,
                        amount = amountValue,
                        note = note.takeIf { it.isNotBlank() },
                        date = Date(),
                        categoryId = selectedCategoryId!!,
                        type = selectedType
                    )
                    
                    // Save transaction
                    if (transactionId == null) {
                        viewModel.addTransaction(transaction)
                    } else {
                        viewModel.updateTransaction(transaction)
                    }
                    
                    onBackClick()
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (transactionId != null) 
                        stringResource(R.string.update)
                    else 
                        stringResource(R.string.save)
                )
            }
        }
    }

    if (showCategoryDialog) {
        CategorySelectionDialog(
            categories = categories,
            onCategorySelected = { category ->
                selectedCategoryId = category.id
                selectedCategoryName = category.name
                showCategoryDialog = false
            },
            onDismiss = { showCategoryDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectionDialog(
    categories: List<com.example.moneytracker.data.local.entities.Category>,
    onCategorySelected: (com.example.moneytracker.data.local.entities.Category) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_category)) },
        text = {
            LazyColumn {
                items(categories) { category ->
                    Card(
                        onClick = { onCategorySelected(category) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.back))
            }
        }
    )
}
