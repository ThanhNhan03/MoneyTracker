package com.example.moneytracker.presentation.screens.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.moneytracker.presentation.components.*
import com.example.moneytracker.presentation.viewmodel.TransactionViewModel
import com.example.moneytracker.ui.theme.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTransactionsScreen(
    viewModel: TransactionViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onTransactionClick: (Transaction) -> Unit = {}
) {    val transactions by viewModel.allTransactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var selectedFilter by remember { mutableStateOf("all") } // all, income, expense
    var showFilterMenu by remember { mutableStateOf(false) }
    
    // Date range filter states
    var selectedDateRange by remember { mutableStateOf("all") }
    var customStartDate by remember { mutableStateOf<LocalDate?>(null) }
    var customEndDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDateFilter by remember { mutableStateOf(false) }    // Load all transactions when screen opens
    LaunchedEffect(Unit) {
        viewModel.loadAllTransactions()
        viewModel.loadAllCategories()
    }

    // Helper function to filter transactions by date range
    fun filterTransactionsByDateRange(transactions: List<Transaction>): List<Transaction> {
        return when (selectedDateRange) {
            "today" -> {
                val today = LocalDate.now()
                val todayStart = Date(today.atStartOfDay().toInstant(java.time.ZoneOffset.UTC).toEpochMilli())
                val todayEnd = Date(today.atTime(23, 59, 59).toInstant(java.time.ZoneOffset.UTC).toEpochMilli())
                transactions.filter { it.date >= todayStart && it.date <= todayEnd }
            }            "this_week" -> {
                val today = LocalDate.now()
                val weekFields = WeekFields.of(Locale("vi", "VN"))
                val startOfWeek = today.with(weekFields.dayOfWeek(), 1)
                val endOfWeek = today.with(weekFields.dayOfWeek(), 7)
                val weekStart = Date(startOfWeek.atStartOfDay().toInstant(java.time.ZoneOffset.UTC).toEpochMilli())
                val weekEnd = Date(endOfWeek.atTime(23, 59, 59).toInstant(java.time.ZoneOffset.UTC).toEpochMilli())
                transactions.filter { it.date >= weekStart && it.date <= weekEnd }
            }
            "this_month" -> {
                val today = LocalDate.now()
                val startOfMonth = today.withDayOfMonth(1)
                val endOfMonth = YearMonth.from(today).atEndOfMonth()
                val monthStart = Date(startOfMonth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC).toEpochMilli())
                val monthEnd = Date(endOfMonth.atTime(23, 59, 59).toInstant(java.time.ZoneOffset.UTC).toEpochMilli())
                transactions.filter { it.date >= monthStart && it.date <= monthEnd }
            }
            "last_month" -> {
                val today = LocalDate.now()
                val startOfLastMonth = today.withDayOfMonth(1).minusMonths(1)
                val endOfLastMonth = YearMonth.from(startOfLastMonth).atEndOfMonth()
                val monthStart = Date(startOfLastMonth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC).toEpochMilli())
                val monthEnd = Date(endOfLastMonth.atTime(23, 59, 59).toInstant(java.time.ZoneOffset.UTC).toEpochMilli())
                transactions.filter { it.date >= monthStart && it.date <= monthEnd }
            }
            "custom" -> {
                if (customStartDate != null && customEndDate != null) {
                    val start = Date(customStartDate!!.atStartOfDay().toInstant(java.time.ZoneOffset.UTC).toEpochMilli())
                    val end = Date(customEndDate!!.atTime(23, 59, 59).toInstant(java.time.ZoneOffset.UTC).toEpochMilli())
                    transactions.filter { it.date >= start && it.date <= end }
                } else {
                    transactions
                }
            }
            else -> transactions
        }
    }

    // Filter transactions based on selected filter and date range
    val filteredTransactions = when (selectedFilter) {
        "income" -> filterTransactionsByDateRange(transactions).filter { it.type == "income" }
        "expense" -> filterTransactionsByDateRange(transactions).filter { it.type == "expense" }
        else -> filterTransactionsByDateRange(transactions)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.all_transactions),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },                actions = {
                    // Date filter button
                    IconButton(onClick = { showDateFilter = !showDateFilter }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Lọc theo ngày"
                        )
                    }
                    
                    // Filter button
                    Box {
                        IconButton(onClick = { showFilterMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = stringResource(R.string.filter)
                            )
                        }
                        
                        FilterDropdownMenu(
                            expanded = showFilterMenu,
                            selectedFilter = selectedFilter,
                            onDismissRequest = { showFilterMenu = false },
                            onFilterSelected = { filter ->
                                selectedFilter = filter
                                showFilterMenu = false
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)        ) {
            // Date range filter
            if (showDateFilter) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Lọc theo thời gian",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        DateRangeFilter(
                            selectedRange = selectedDateRange,
                            customStartDate = customStartDate,
                            customEndDate = customEndDate,
                            onRangeSelected = { range ->
                                selectedDateRange = range
                                if (range != "custom") {
                                    customStartDate = null
                                    customEndDate = null
                                }
                            },
                            onCustomDateSelected = { start, end ->
                                customStartDate = start
                                customEndDate = end
                            }
                        )
                    }
                }            }

                when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Lỗi: $error",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }                filteredTransactions.isEmpty() -> {
                    EmptyTransactionsView(
                        selectedFilter = selectedFilter,
                        selectedDateRange = selectedDateRange
                    )
                }
                else -> {
                    AllTransactionsList(
                        transactions = filteredTransactions,
                        categories = categories,
                        onTransactionClick = onTransactionClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }        }
    }
}
