package com.example.moneytracker.presentation.screens.add

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneytracker.R
import com.example.moneytracker.data.local.entities.Transaction
import com.example.moneytracker.presentation.viewmodel.TransactionViewModel
import com.example.moneytracker.presentation.viewmodel.CategoryViewModel
import com.example.moneytracker.ui.theme.*
import com.example.moneytracker.util.NumberFormatter
import java.util.*
import android.util.Log

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
            amount = NumberFormatter.formatNumber(transaction.amount.toString())
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Transaction Type Selection - Modern Card Style
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Loại giao dịch",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            listOf(
                                "expense" to stringResource(R.string.expense),
                                "income" to stringResource(R.string.income)
                            ).forEach { (type, label) ->
                                Card(
                                    onClick = { selectedType = type },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (type == selectedType) {
                                            if (type == "income") IncomeGreen else ExpenseRed
                                        } else {
                                            MaterialTheme.colorScheme.surface
                                        }
                                    ),
                                    border = if (type == selectedType) null else BorderStroke(
                                        1.dp, 
                                        MaterialTheme.colorScheme.outline
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = if (type == selectedType) {
                                                Color.White
                                            } else {
                                                MaterialTheme.colorScheme.onSurface
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Amount Input - Enhanced
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Số tiền",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { newValue ->
                                // Only allow digits and format with commas
                                val digitsOnly = newValue.replace(Regex("[^\\d]"), "")
                                if (digitsOnly.length <= 12) { // Limit to reasonable number length
                                    amount = if (digitsOnly.isNotEmpty()) {
                                        NumberFormatter.formatNumber(digitsOnly)
                                    } else {
                                        ""
                                    }
                                }
                            },
                            label = { Text("Nhập số tiền") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default.copy(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            ),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AttachMoney,
                                    contentDescription = "Số tiền",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            ),
                            placeholder = { Text("0") }
                        )
                    }
                }
            }

            // Category Selection - Enhanced
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Danh mục",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        OutlinedButton(
                            onClick = { showCategoryDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(
                                1.dp, 
                                if (selectedCategoryId != null) MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.outline
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Category,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = if (selectedCategoryId != null) MaterialTheme.colorScheme.primary 
                                       else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = selectedCategoryName.ifEmpty { "Chọn danh mục" },
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (selectedCategoryId != null) MaterialTheme.colorScheme.primary 
                                       else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Note Input - Enhanced
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Ghi chú",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            label = { Text("Ghi chú (không bắt buộc)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = false,
                            minLines = 3,
                            maxLines = 4,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Note,
                                    contentDescription = "Ghi chú",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }

            // Save Button - Enhanced
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        Log.d("AddTransaction", "Save button clicked")
                        Log.d("AddTransaction", "Amount input: '$amount'")
                        Log.d("AddTransaction", "Selected category ID: $selectedCategoryId")
                        
                        // Validate input
                        val amountValue = NumberFormatter.getDoubleValue(amount)
                        Log.d("AddTransaction", "Parsed amount value: $amountValue")
                        
                        if (amountValue == null || amountValue <= 0) {
                            Log.e("AddTransaction", "Invalid amount: $amountValue")
                            viewModel.setError("error_invalid_amount")
                            return@Button
                        }
                        if (selectedCategoryId == null) {
                            Log.e("AddTransaction", "No category selected")
                            viewModel.setError("error_category_required")
                            return@Button
                        }
                        
                        Log.d("AddTransaction", "Creating transaction with amount: $amountValue, category: $selectedCategoryId, type: $selectedType")
                        
                        // Create transaction
                        val transaction = Transaction(
                            id = transactionId ?: 0,
                            amount = amountValue,
                            note = note.takeIf { it.isNotBlank() },
                            date = Date(),
                            categoryId = selectedCategoryId!!,
                            type = selectedType
                        )
                        
                        Log.d("AddTransaction", "Transaction created: $transaction")
                        
                        // Save transaction
                        if (transactionId == null) {
                            Log.d("AddTransaction", "Adding new transaction")
                            viewModel.addTransaction(transaction)
                        } else {
                            Log.d("AddTransaction", "Updating existing transaction")
                            viewModel.updateTransaction(transaction)
                        }
                        
                        Log.d("AddTransaction", "Calling onBackClick")
                        onBackClick()
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 4.dp
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (transactionId != null) 
                                stringResource(R.string.update)
                            else 
                                stringResource(R.string.save),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
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
