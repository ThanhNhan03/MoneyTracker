package com.example.moneytracker.presentation.screens.report

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.moneytracker.R
import com.example.moneytracker.presentation.components.TransactionChart
import com.example.moneytracker.presentation.components.DateSelector
import com.example.moneytracker.util.toVND
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneytracker.presentation.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    viewModel: TransactionViewModel = hiltViewModel()
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    
    val startOfMonth = remember(selectedDate) { selectedDate.withDayOfMonth(1) }
    val endOfMonth = remember(selectedDate) { YearMonth.from(startOfMonth).atEndOfMonth() }
    
    // Load transactions for selected month
    LaunchedEffect(selectedDate) {
        viewModel.loadTransactions(
            startDate = Date(startOfMonth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC).toEpochMilli()),
            endDate = Date(endOfMonth.atTime(23, 59, 59).toInstant(java.time.ZoneOffset.UTC).toEpochMilli())
        )
    }

    val transactions by viewModel.transactions.collectAsState()
    val totalIncome = transactions.filter { it.type == "income" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == "expense" }.sumOf { it.amount }
    val balance = totalIncome - totalExpense
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(stringResource(id = R.string.reports))
                        DateSelector(
                            selectedDate = selectedDate,
                            onDateSelected = { date -> selectedDate = date },
                            showMonthYear = true
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
                .padding(16.dp)
        ) {
            // Summary Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Income Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.income),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "+ ${totalIncome.toVND()}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                // Expense Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.expense),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "- ${totalExpense.toVND()}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Balance Card
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.total_balance),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${balance.toVND()}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    if (totalIncome > 0) {
                        val percentage = (balance.toDouble() / totalIncome * 100).toInt()
                        Text(
                            text = "${if (percentage >= 0) "+" else ""}$percentage% ${stringResource(R.string.of_income)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (percentage >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Expense by Category Chart
            Text(
                text = stringResource(R.string.expense_by_category),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Chart will be implemented here
            TransactionChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}
