package com.example.moneytracker.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moneytracker.data.local.entities.Category
import com.example.moneytracker.data.local.entities.Transaction
import com.example.moneytracker.ui.theme.*
import com.example.moneytracker.util.toVND
import com.example.moneytracker.util.DateUtils
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AllTransactionsList(
    transactions: List<Transaction>,
    categories: List<Category>,
    onTransactionClick: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {    // Group transactions by date
    val groupedTransactions = transactions
        .sortedByDescending { it.date }
        .groupBy {
            DateUtils.formatDateVietnamese(it.date)
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
                            color = if (dayTotal >= 0) IncomeGreen else ExpenseRed
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
