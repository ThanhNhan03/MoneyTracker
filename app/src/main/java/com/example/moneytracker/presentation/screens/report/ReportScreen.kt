package com.example.moneytracker.presentation.screens.report

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.moneytracker.R
import com.example.moneytracker.presentation.components.*
import com.example.moneytracker.presentation.components.MonthYearPicker
import com.example.moneytracker.presentation.viewmodel.TransactionViewModel
import com.example.moneytracker.data.remote.GeminiAiService
import com.example.moneytracker.data.local.entities.Transaction
import com.example.moneytracker.data.local.entities.Category
import com.example.moneytracker.ui.theme.*
import com.example.moneytracker.util.toVND
import java.time.LocalDate
import java.time.YearMonth
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    viewModel: TransactionViewModel = hiltViewModel(),
    geminiService: GeminiAiService? = null
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    
    val startOfMonth = remember(selectedDate) { selectedDate.withDayOfMonth(1) }
    val endOfMonth = remember(selectedDate) { YearMonth.from(startOfMonth).atEndOfMonth() }
    
    val allTransactions by viewModel.allTransactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // Filter transactions for the selected month/year
    val filteredTransactions = remember(allTransactions, selectedDate) {
        if (allTransactions.isEmpty()) {
            emptyList()
        } else {
            val selectedYear = selectedDate.year
            val selectedMonth = selectedDate.monthValue
            
            allTransactions.filter { transaction ->
                val calendar = Calendar.getInstance().apply {
                    time = transaction.date
                }
                val transactionYear = calendar.get(Calendar.YEAR)
                val transactionMonth = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH is 0-based
                
                transactionYear == selectedYear && transactionMonth == selectedMonth
            }
        }
    }
    
    val totalIncome = filteredTransactions.filter { it.type == "income" }.sumOf { it.amount }
    val totalExpense = filteredTransactions.filter { it.type == "expense" }.sumOf { it.amount }
    val balance = totalIncome - totalExpense
    
    // Load all data once when screen is opened
    LaunchedEffect(Unit) {
        viewModel.loadAllTransactions()
        viewModel.loadAllCategories()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(stringResource(id = R.string.reports))
                        
                        // Navigation buttons and picker
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Previous month button
                            IconButton(
                                onClick = {
                                    selectedDate = selectedDate.minusMonths(1)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowLeft,
                                    contentDescription = "Tháng trước"
                                )
                            }
                            
                            // Month/Year picker
                            MonthYearPicker(
                                selectedDate = selectedDate,
                                onDateSelected = { date -> selectedDate = date }
                            )
                            
                            // Next month button  
                            IconButton(
                                onClick = {
                                    selectedDate = selectedDate.plusMonths(1)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = "Tháng sau"
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Month/Year indicator with transaction count
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Báo cáo tháng ${selectedDate.monthValue}/${selectedDate.year}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${filteredTransactions.size} giao dịch",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
                
                // Summary Cards
                SummaryCards(
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    balance = balance
                )
                
                // Financial Insights
                FinancialInsights(
                    transactions = filteredTransactions,
                    categories = categories,
                    selectedDate = selectedDate,
                    geminiService = geminiService
                )
                
                // Expense by Category Chart
                ExpenseByCategorySection(
                    transactions = filteredTransactions,
                    categories = categories
                )
            }
        }
    }
}



@Composable
private fun SummaryCards(
    totalIncome: Double,
    totalExpense: Double,
    balance: Double
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Income and Expense Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Income Card
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = IncomeGreen.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.income),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "+ ${totalIncome.toVND()}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = IncomeGreen
                    )
                }
            }
            
            // Expense Card
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = ExpenseRed.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.expense),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "- ${totalExpense.toVND()}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = ExpenseRed
                    )
                }
            }
        }
        
        // Balance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (balance >= 0) 
                    Primary.copy(alpha = 0.1f)
                else 
                    ExpenseRed.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.total_balance),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = balance.toVND(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (balance >= 0) 
                        Primary
                    else 
                        ExpenseRed
                )
                if (totalIncome > 0) {
                    val percentage = (balance / totalIncome * 100).toInt()
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${if (percentage >= 0) "+" else ""}$percentage% ${stringResource(R.string.of_income)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpenseByCategorySection(
    transactions: List<com.example.moneytracker.data.local.entities.Transaction>,
    categories: List<com.example.moneytracker.data.local.entities.Category>
) {
    Column {
        Text(
            text = stringResource(R.string.expense_by_category),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        ExpenseByCategoryChart(
            transactions = transactions,
            categories = categories,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ReportScreenWithAi(
    viewModel: TransactionViewModel = hiltViewModel()
) {
    // Inject GeminiAiService through Hilt
    val geminiService: GeminiAiService = hiltViewModel<ReportViewModel>().geminiService
    
    ReportScreen(
        viewModel = viewModel,
        geminiService = geminiService
    )
}

// Helper ViewModel to inject GeminiAiService
@HiltViewModel
class ReportViewModel @Inject constructor(
    val geminiService: GeminiAiService
) : ViewModel()

// ========== PREVIEW COMPOSABLES ==========

@Preview(showBackground = true, name = "Summary Cards Preview")
@Composable
private fun SummaryCardsPreview() {
    MaterialTheme {
        SummaryCards(
            totalIncome = 15000000.0,
            totalExpense = 8500000.0,
            balance = 6500000.0
        )
    }
}

@Preview(showBackground = true, name = "Summary Cards Negative Balance")
@Composable
private fun SummaryCardsNegativeBalancePreview() {
    MaterialTheme {
        SummaryCards(
            totalIncome = 5000000.0,
            totalExpense = 7500000.0,
            balance = -2500000.0
        )
    }
}

@Preview(showBackground = true, name = "Expense by Category Section")
@Composable
private fun ExpenseByCategorySectionPreview() {
    val sampleTransactions = listOf(
        Transaction(
            id = 1,
            amount = 500000.0,
            note = "Ăn trưa",
            date = Date(),
            type = "expense",
            categoryId = 1
        ),
        Transaction(
            id = 2,
            amount = 200000.0,
            note = "Café",
            date = Date(),
            type = "expense",
            categoryId = 1
        ),
        Transaction(
            id = 3,
            amount = 800000.0,
            note = "Xăng xe",
            date = Date(),
            type = "expense",
            categoryId = 2
        ),
        Transaction(
            id = 4,
            amount = 1500000.0,
            note = "Điện nước",
            date = Date(),
            type = "expense",
            categoryId = 3
        )
    )
    
    val sampleCategories = listOf(
        Category(id = 1, name = "Ăn uống", type = "expense"),
        Category(id = 2, name = "Di chuyển", type = "expense"),
        Category(id = 3, name = "Hóa đơn", type = "expense")
    )
    
    MaterialTheme {
        ExpenseByCategorySection(
            transactions = sampleTransactions,
            categories = sampleCategories
        )
    }
}

@Preview(showBackground = true, name = "Report Screen Content")
@Composable
private fun ReportScreenContentPreview() {
    val sampleTransactions = listOf(
        Transaction(
            id = 1,
            amount = 500000.0,
            note = "Ăn trưa",
            date = Date(),
            type = "expense",
            categoryId = 1
        ),
        Transaction(
            id = 2,
            amount = 2000000.0,
            note = "Lương",
            date = Date(),
            type = "income",
            categoryId = 4
        ),
        Transaction(
            id = 3,
            amount = 800000.0,
            note = "Xăng xe",
            date = Date(),
            type = "expense",
            categoryId = 2
        ),
        Transaction(
            id = 4,
            amount = 1500000.0,
            note = "Điện nước",
            date = Date(),
            type = "expense",
            categoryId = 3
        ),
        Transaction(
            id = 5,
            amount = 500000.0,
            note = "Thưởng",
            date = Date(),
            type = "income",
            categoryId = 4
        )
    )
    
    val sampleCategories = listOf(
        Category(id = 1, name = "Ăn uống", type = "expense"),
        Category(id = 2, name = "Di chuyển", type = "expense"),
        Category(id = 3, name = "Hóa đơn", type = "expense"),
        Category(id = 4, name = "Lương", type = "income")
    )
    
    val totalIncome = sampleTransactions.filter { it.type == "income" }.sumOf { it.amount }
    val totalExpense = sampleTransactions.filter { it.type == "expense" }.sumOf { it.amount }
    val balance = totalIncome - totalExpense
    
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Month/Year indicator with transaction count
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Báo cáo tháng 6/2025",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${sampleTransactions.size} giao dịch",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
            
            // Summary Cards
            SummaryCards(
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                balance = balance
            )
            
            // Financial Insights (mock)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "💡 Phân tích tài chính",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Thu nhập tháng này khá ổn định! 📈✨",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            // Expense by Category Chart
            ExpenseByCategorySection(
                transactions = sampleTransactions,
                categories = sampleCategories
            )
        }
    }
}
