package com.example.moneytracker.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneytracker.R
import com.example.moneytracker.presentation.components.TransactionList
import com.example.moneytracker.presentation.viewmodel.TransactionViewModel
import com.example.moneytracker.util.toVND
import java.util.*
import java.text.SimpleDateFormat
import java.text.NumberFormat
import java.util.Locale

import com.example.moneytracker.data.local.entities.Transaction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TransactionViewModel = hiltViewModel(),
    onAddTransactionClick: () -> Unit,
    onTransactionClick: (Transaction) -> Unit = {}
) {
    val transactions by viewModel.transactions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val errorMessage =  stringResource(R.string.error_invalid_amount)
    val balance by viewModel.balance.collectAsState()
    var showAddBalanceDialog by remember { mutableStateOf(false) }


    // Calculate totals
    val totalIncome = transactions
        .filter { it.type == "income" }
        .sumOf { it.amount }
    val totalExpense = transactions
        .filter { it.type == "expense" }
        .sumOf { it.amount }
    val currentBalance = (balance?.amount ?: 0.0) + totalIncome - totalExpense

    // Load transactions for the current month
    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
        val startDate = calendar.apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }.time
        val endDate = Calendar.getInstance().time
        viewModel.loadTransactions(startDate, endDate)
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically  
                ) {
                    IconButton(
                        onClick = { showAddBalanceDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = stringResource(R.string.add_balance),
                            tint = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Balance Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.total_balance),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Text(
                            text = currentBalance.toVND(),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.income),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                                Text(
                                    text = totalIncome.toVND(),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Green
                                    )
                                )
                            }
                            Column {
                                Text(
                                    text = stringResource(R.string.expenses),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                                Text(
                                    text = totalExpense.toVND(),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Red
                                    )
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransactionClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_transaction),
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.recent_transactions),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(vertical = 16.dp)
            )
            
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Text(
                    text = stringResource(R.string.error) + ": ${error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                TransactionList(
                    transactions = transactions,
                    onTransactionClick = onTransactionClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    if (showAddBalanceDialog) {
        var balanceAmount by remember { mutableStateOf("") }
        var balanceError by remember { mutableStateOf<String?>(null) }
        
        val format = NumberFormat.getNumberInstance(Locale.getDefault())
        val formattedAmount = try {
            if (balanceAmount.isNotEmpty()) {
                format.format(balanceAmount.replace(".", "").toLong())
            } else ""
        } catch (e: Exception) {
            balanceAmount 
        }

        AlertDialog(
            onDismissRequest = { 
                showAddBalanceDialog = false
                balanceAmount = ""
                balanceError = null
            },
            title = { Text(stringResource(R.string.add_balance)) },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.amount),
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formattedAmount + " đ",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = balanceAmount,
                        onValueChange = { newValue ->
                            val cleanedValue = newValue.replace(".", "") // Remove existing dots for parsing
                            if (cleanedValue.matches(Regex("^\\d*$"))) {
                                balanceAmount = cleanedValue
                                balanceError = null
                            }
                        },
                        label = { Text(stringResource(R.string.add_balance)) },
                        singleLine = true,
                        isError = balanceError != null,
                        supportingText = {
                            if (balanceError != null) {
                                Text(balanceError!!)
                            }
                        },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default.copy(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val quickAmounts = listOf(100000.0, 200000.0, 500000.0, 1000000.0, 2000000.0, 5000000.0)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        quickAmounts.chunked(3).forEach { rowAmounts ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                rowAmounts.forEach { amount ->
                                    Button(
                                        onClick = {
                                            balanceAmount = amount.toLong().toString()
                                            balanceError = null
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = format.format(amount))
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        try {
                            val amount = balanceAmount.replace(".", "").toLongOrNull()
                            if (amount == null || amount <= 0) {
                                balanceError = errorMessage
                                return@TextButton
                            }
                            
                            viewModel.updateBalance(amount.toDouble())
                            
                            showAddBalanceDialog = false
                            balanceAmount = ""
                            balanceError = null
                        } catch (e: Exception) {
                            balanceError = errorMessage
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showAddBalanceDialog = false
                        balanceAmount = ""
                        balanceError = null
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
