package com.example.moneytracker.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneytracker.R
import com.example.moneytracker.presentation.components.*
import com.example.moneytracker.presentation.viewmodel.TransactionViewModel
import com.example.moneytracker.ui.theme.*
import com.example.moneytracker.util.toVND
import java.util.*
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import com.example.moneytracker.data.local.entities.Transaction
import android.util.Log
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.NoteAlt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TransactionViewModel = hiltViewModel(),
    onAddTransactionClick: () -> Unit,
    onTransactionClick: (Transaction) -> Unit = {},
    onSeeAllTransactionsClick: () -> Unit = {}
) {    val transactions by viewModel.transactions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val categories by viewModel.categories.collectAsState()

    // Calculate totals from current month transactions only
    val totalIncome = transactions
        .filter { it.type == "income" }
        .sumOf { it.amount }
    val totalExpense = transactions
        .filter { it.type == "expense" }
        .sumOf { it.amount }
    
    // Current balance = income - expense for current month (like in ReportScreen)
    val currentBalance = totalIncome - totalExpense    // Load transactions for the current month
    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
        val startDate = calendar.apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
        val endDate = calendar.apply {
            set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.time
        
        Log.d("HomeScreen", "Loading transactions from ${startDate} to ${endDate}")
        viewModel.loadTransactions(startDate, endDate)
        // Force reload categories to ensure they're available
        viewModel.loadAllCategories()
    }
    
    // Debug transactions
    LaunchedEffect(transactions) {
        Log.d("HomeScreen", "Transactions loaded: ${transactions.size}")
        Log.d("HomeScreen", "Total Income: $totalIncome")
        Log.d("HomeScreen", "Total Expense: $totalExpense") 
        Log.d("HomeScreen", "Current Balance: $currentBalance")
        transactions.forEach { transaction ->
            Log.d("HomeScreen", "Transaction: ${transaction.note} - ${transaction.amount} - ${transaction.date} - ${transaction.type}")
        }
    }
    
    // Debug categories
    LaunchedEffect(categories) {
        Log.d("HomeScreen", "Categories loaded: ${categories.size}")
        categories.forEach { category ->
            Log.d("HomeScreen", "Category: id=${category.id}, name=${category.name}, type=${category.type}")
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransactionClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_transaction),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else if (error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.error) + ": ${error}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Balance Card Section - now scrollable
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        BalanceCard(
                            currentBalance = currentBalance,
                            totalIncome = totalIncome,
                            totalExpense = totalExpense
                        )
                    }
                }

                // Spending Overview Section
                if (transactions.filter { it.type == "expense" }.isNotEmpty()) {
                    item {
                        SpendingOverviewSection(
                            expenseTransactions = transactions.filter { it.type == "expense" },
                            categories = categories
                        )
                    }
                } else {
                    item {
                        NoExpenseDataCard()
                    }
                }
                  // Recent Transactions Section
                item {
                    RecentTransactionsSection(
                        transactions = transactions.take(4), // Show only first 4 transactions
                        onTransactionClick = onTransactionClick,
                        onSeeAllClick = onSeeAllTransactionsClick,
                        categories = categories
                    )
                }
            }
        }
    }
}

@Composable
fun SpendingOverviewSection(
    expenseTransactions: List<Transaction>,
    categories: List<com.example.moneytracker.data.local.entities.Category>
) {
    // Group expenses by category and calculate percentages
    val categoryExpenses = expenseTransactions.groupBy { it.categoryId }
        .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }
    
    val totalExpense = categoryExpenses.values.sum()
    val topCategories: List<Pair<Long, Double>> = categoryExpenses.entries
        .sortedByDescending { it.value }
        .take(3)
        .map { it.key.toLong() to it.value } // Explicitly cast key to Long
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.spending_overview),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
              // Update the forEach loop to explicitly destructure the Pair
            for ((categoryId, amount) in topCategories) {
                val percentage = if (totalExpense > 0) (amount / totalExpense * 100).toInt() else 0
                val categoryName = categories.find { it.id == categoryId.toInt() }?.name ?: "Category $categoryId"
                
                Log.d("SpendingOverview", "Looking for categoryId: $categoryId, found: ${categories.find { it.id == categoryId.toInt() }?.name}")
                Log.d("SpendingOverview", "Available categories: ${categories.map { "${it.id}:${it.name}" }}")
                
                SpendingProgressItem(
                    categoryName = categoryName,
                    amount = amount,
                    percentage = percentage,
                    color = when (topCategories.indexOf(categoryId to amount)) {
                        0 -> CategoryColor1 // Pink
                        1 -> CategoryColor4 // Indigo
                        else -> CategoryColor2 // Purple
                    }
                )
                
                if (topCategories.indexOf(categoryId to amount) < topCategories.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun SpendingProgressItem(
    categoryName: String,
    amount: Double,
    percentage: Int,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = categoryName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = percentage / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun RecentTransactionsSection(
    transactions: List<Transaction>,
    onTransactionClick: (Transaction) -> Unit,
    onSeeAllClick: () -> Unit,
    categories: List<com.example.moneytracker.data.local.entities.Category>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.recent_transactions),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                
                TextButton(onClick = onSeeAllClick) {
                    Text(
                        text = stringResource(R.string.see_all),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (transactions.isEmpty()) {
                val currentMonth = java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale("vi", "VN"))
                    .format(java.util.Date())
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.NoteAlt,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Chưa có giao dịch nào trong  $currentMonth",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Nhấn nút + để thêm giao dịch đầu tiên!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                transactions.forEach { transaction ->
                    RecentTransactionItem(
                        transaction = transaction,
                        onClick = { onTransactionClick(transaction) },
                        categories = categories
                    )
                    
                    if (transactions.indexOf(transaction) < transactions.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentTransactionItem(
    transaction: Transaction,    onClick: () -> Unit,
    categories: List<com.example.moneytracker.data.local.entities.Category>
) {
    val categoryName = categories.find { it.id == transaction.categoryId }?.name ?: "Category ${transaction.categoryId}"
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Transaction icon based on type - using + and - symbols
                Surface(
                    modifier = Modifier
                        .size(32.dp),
                    shape = CircleShape,
                    color = if (transaction.type == "income") 
                        IncomeGreen.copy(alpha = 0.2f) 
                    else 
                        ExpenseRed.copy(alpha = 0.2f)
                ) {
                    Icon(
                        imageVector = if (transaction.type == "income") 
                            Icons.Default.Add 
                        else 
                            Icons.Default.Remove,
                        contentDescription = null,
                        tint = if (transaction.type == "income") 
                            IncomeGreen 
                        else 
                            ExpenseRed,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = transaction.note ?: categoryName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formatDateInVietnamese(transaction.date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Text(
                text = if (transaction.type == "income") 
                    "+${transaction.amount.toVND()}" 
                else 
                    "-${transaction.amount.toVND()}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = if (transaction.type == "income") 
                    IncomeGreen 
                else 
                    ExpenseRed
            )

        }
    }
}

// Helper function to format date in Vietnamese
private fun formatDateInVietnamese(date: Date): String {
    val calendar = Calendar.getInstance()
    val transactionCalendar = Calendar.getInstance()
    transactionCalendar.time = date
    
    val currentDate = calendar.time
    val daysDifference = TimeUnit.DAYS.convert(
        currentDate.time - date.time,
        TimeUnit.MILLISECONDS
    )
    
    return when {
        daysDifference == 0L -> "Hôm nay"
        daysDifference == 1L -> "Hôm qua"
        daysDifference < 7L -> "$daysDifference ngày trước"
        else -> {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))
            formatter.format(date)
        }
    }
}

@Composable
fun NoExpenseDataCard() {
    val currentMonth = java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale("vi", "VN"))
        .format(java.util.Date())
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.BarChart,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.spending_overview),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Chưa có chi tiêu nào trong  $currentMonth",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Hãy bắt đầu ghi chép để theo dõi chi tiêu!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}


