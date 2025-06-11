package com.example.moneytracker.presentation.screens.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneytracker.R
import com.example.moneytracker.presentation.viewmodel.TransactionViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onBackClick: () -> Unit,
    transactionId: Int? = null,
    viewModel: TransactionViewModel = hiltViewModel()
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
    
    // Observe the transaction to edit
    val transactionToEdit by viewModel.transactionToEdit.collectAsState()
    
    // Update UI when transaction data is loaded
    LaunchedEffect(transactionToEdit) {
        transactionToEdit?.let { transaction ->
            amount = transaction.amount.toString()
            note = transaction.note ?: ""
            selectedType = transaction.type
            selectedCategoryId = transaction.categoryId
            // TODO: Load category name from categoryId
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
                )
            )

            // Category Selection
            OutlinedButton(
                onClick = { showCategoryDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
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
                maxLines = 3
            )

            // Save Button
            Button(
                onClick = {
                    // TODO: Validate input and save transaction
                    onBackClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
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
        // TODO: Implement category selection dialog
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { Text(stringResource(R.string.select_category)) },
            text = { Text(stringResource(R.string.no_data)) },
            confirmButton = {
                TextButton(
                    onClick = { showCategoryDialog = false }
                ) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }
}
