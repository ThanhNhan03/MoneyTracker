package com.example.moneytracker.presentation.screens.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneytracker.R
import com.example.moneytracker.data.local.entities.Transaction
import com.example.moneytracker.presentation.components.TransactionList
import com.example.moneytracker.presentation.viewmodel.TransactionViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTransactionsScreen(
    onBackClick: () -> Unit,
    onTransactionClick: (Transaction) -> Unit = {},
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val transactions by viewModel.transactions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var selectedFilter by remember { mutableStateOf("all") }
    
    // Load all transactions
    LaunchedEffect(Unit) {
        // Load transactions for the last 3 months
        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        calendar.add(Calendar.MONTH, -3)
        val startDate = calendar.time
        viewModel.loadTransactions(startDate, endDate)
    }
    
    // Filter transactions based on selected filter
    val filteredTransactions = when (selectedFilter) {
        "income" -> transactions.filter { it.type == "income" }
        "expense" -> transactions.filter { it.type == "expense" }
        else -> transactions
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.all_transactions),
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack, 
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Show filter dialog */ }) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filter"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter tabs
            ScrollableTabRow(
                selectedTabIndex = when (selectedFilter) {
                    "all" -> 0
                    "income" -> 1
                    "expense" -> 2
                    else -> 0
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedFilter == "all",
                    onClick = { selectedFilter = "all" },
                    text = { Text(stringResource(R.string.all)) }
                )
                Tab(
                    selected = selectedFilter == "income",
                    onClick = { selectedFilter = "income" },
                    text = { Text(stringResource(R.string.income)) }
                )
                Tab(
                    selected = selectedFilter == "expense",
                    onClick = { selectedFilter = "expense" },
                    text = { Text(stringResource(R.string.expense)) }
                )
            }
            
            // Content
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.error) + ": ${error}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else if (filteredTransactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_transactions),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                TransactionList(
                    transactions = filteredTransactions,
                    onTransactionClick = onTransactionClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
