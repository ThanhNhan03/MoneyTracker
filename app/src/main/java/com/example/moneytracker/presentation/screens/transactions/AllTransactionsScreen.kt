package com.example.moneytracker.presentation.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneytracker.R
import com.example.moneytracker.data.local.entities.Transaction
import com.example.moneytracker.presentation.viewmodel.TransactionViewModel
import com.example.moneytracker.util.toVND
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTransactionsScreen(
    viewModel: TransactionViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onTransactionClick: (Transaction) -> Unit = {}
) {
    val transactions by viewModel.allTransactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var selectedFilter by remember { mutableStateOf("all") } // all, income, expense
    var showFilterMenu by remember { mutableStateOf(false) }

    // Load all transactions when screen opens
    LaunchedEffect(Unit) {
        viewModel.loadAllTransactions()
        viewModel.loadAllCategories()
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
                },
                actions = {
                    // Filter button
                    Box {
                        IconButton(onClick = { showFilterMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = stringResource(R.string.filter)
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showFilterMenu,
                            onDismissRequest = { showFilterMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Tất cả") },
                                onClick = {
                                    selectedFilter = "all"
                                    showFilterMenu = false
                                },
                                leadingIcon = {
                                    if (selectedFilter == "all") {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Thu nhập") },
                                onClick = {
                                    selectedFilter = "income"
                                    showFilterMenu = false
                                },
                                leadingIcon = {
                                    if (selectedFilter == "income") {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Chi tiêu") },
                                onClick = {
                                    selectedFilter = "expense"
                                    showFilterMenu = false
                                },
                                leadingIcon = {
                                    if (selectedFilter == "expense") {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                    }
                                }
                            )
                        }
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
            // Summary card
            if (filteredTransactions.isNotEmpty()) {
                SummaryCard(
                    transactions = filteredTransactions,
                    filterType = selectedFilter,
                    modifier = Modifier.padding(16.dp)
                )
            }

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
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
                }
                filteredTransactions.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = when (selectedFilter) {
                                    "income" -> "Chưa có giao dịch thu nhập"
                                    "expense" -> "Chưa có giao dịch chi tiêu"
                                    else -> "Chưa có giao dịch nào"
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    TransactionsList(
                        transactions = filteredTransactions,
                        categories = categories,
                        onTransactionClick = onTransactionClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryCard(
    transactions: List<Transaction>,
    filterType: String,
    modifier: Modifier = Modifier
) {
    val totalIncome = transactions.filter { it.type == "income" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == "expense" }.sumOf { it.amount }
    val netAmount = totalIncome - totalExpense

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = when (filterType) {
                    "income" -> "Tổng thu nhập"
                    "expense" -> "Tổng chi tiêu"
                    else -> "Tổng quan"
                },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            when (filterType) {
                "income" -> {
                    SummaryItem(
                        label = "Thu nhập",
                        amount = totalIncome,
                        color = Color(0xFF27AE60),
                        isPositive = true
                    )
                }
                "expense" -> {
                    SummaryItem(
                        label = "Chi tiêu",
                        amount = totalExpense,
                        color = Color(0xFFE74C3C),
                        isPositive = false
                    )
                }
                else -> {
                    SummaryItem(
                        label = "Thu nhập",
                        amount = totalIncome,
                        color = Color(0xFF27AE60),
                        isPositive = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SummaryItem(
                        label = "Chi tiêu",
                        amount = totalExpense,
                        color = Color(0xFFE74C3C),
                        isPositive = false
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                    SummaryItem(
                        label = "Số dư ròng",
                        amount = netAmount,
                        color = if (netAmount >= 0) Color(0xFF27AE60) else Color(0xFFE74C3C),
                        isPositive = netAmount >= 0,
                        isTotal = true
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryItem(
    label: String,
    amount: Double,
    color: Color,
    isPositive: Boolean,
    isTotal: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isTotal) 
                MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            else 
                MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "${if (isPositive) "+" else "-"}${amount.toVND()}",
            style = if (isTotal) 
                MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            else 
                MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = color
        )
    }
}

@Composable
fun TransactionsList(
    transactions: List<Transaction>,
    categories: List<com.example.moneytracker.data.local.entities.Category>,
    onTransactionClick: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    // Group transactions by date
    val groupedTransactions = transactions
        .sortedByDescending { it.date }
        .groupBy {
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it.date)
        }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        groupedTransactions.forEach { (date, dailyTransactions) ->
            item {
                // Date header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = date,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        val dayTotal = dailyTransactions.sumOf { 
                            if (it.type == "income") it.amount else -it.amount 
                        }
                        Text(
                            text = "${if (dayTotal >= 0) "+" else ""}${dayTotal.toVND()}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = if (dayTotal >= 0) Color(0xFF27AE60) else Color(0xFFE74C3C)
                        )
                    }
                }
            }
            
            items(dailyTransactions) { transaction ->
                TransactionItemCard(
                    transaction = transaction,
                    categories = categories,
                    onClick = { onTransactionClick(transaction) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionItemCard(
    transaction: Transaction,
    categories: List<com.example.moneytracker.data.local.entities.Category>,
    onClick: () -> Unit
) {
    val categoryName = categories.find { it.id == transaction.categoryId }?.name 
        ?: "Danh mục ${transaction.categoryId}"

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Transaction icon
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = if (transaction.type == "income") 
                        Color(0xFF27AE60).copy(alpha = 0.2f) 
                    else 
                        Color(0xFFE74C3C).copy(alpha = 0.2f)
                ) {
                    Icon(
                        imageVector = if (transaction.type == "income") 
                            Icons.Default.TrendingUp 
                        else 
                            Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (transaction.type == "income") 
                            Color(0xFF27AE60) 
                        else 
                            Color(0xFFE74C3C),
                        modifier = Modifier.padding(8.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = transaction.note?.takeIf { it.isNotBlank() } ?: categoryName,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
            
            Text(
                text = "${if (transaction.type == "income") "+" else "-"}${transaction.amount.toVND()}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = if (transaction.type == "income") 
                    Color(0xFF27AE60) 
                else 
                    Color(0xFFE74C3C)
            )
        }
    }
}
