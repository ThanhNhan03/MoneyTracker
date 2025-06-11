package com.example.moneytracker.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
    var showAddBalanceDialog by remember { mutableStateOf(false) }

    // Calculate totals
    val totalIncome = transactions
        .filter { it.type == "income" }
        .sumOf { it.amount }
    val totalExpense = transactions
        .filter { it.type == "expense" }
        .sumOf { it.amount }
    val balance = totalIncome - totalExpense

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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.hello_user, "User"),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    IconButton(
                        onClick = { showAddBalanceDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
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
                            text = balance.toVND(),
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
        
        AlertDialog(
            onDismissRequest = { showAddBalanceDialog = false },
            title = { Text(stringResource(R.string.add_balance)) },
            text = {
                OutlinedTextField(
                    value = balanceAmount,
                    onValueChange = { balanceAmount = it },
                    label = { Text(stringResource(R.string.amount)) },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default.copy(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // TODO: Add balance logic
                        showAddBalanceDialog = false
                    }
                ) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddBalanceDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
