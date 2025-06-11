package com.example.moneytracker.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moneytracker.data.local.entities.Transaction
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionList(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier,
    onTransactionClick: (Transaction) -> Unit = {}
) {
    val groupedTransactions = transactions.groupBy {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it.date)
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        groupedTransactions.forEach { (date, dailyTransactions) ->
            item {
                Text(
                    text = date,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            items(dailyTransactions) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = transaction.note ?: "No description",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Category: ${transaction.categoryId}", // TODO: Replace with actual category name
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${if (transaction.type == "expense") "-" else "+"} ${String.format("%.2f", transaction.amount)} ₫",
                style = MaterialTheme.typography.bodyLarge,
                color = if (transaction.type == "expense") MaterialTheme.colorScheme.error 
                       else MaterialTheme.colorScheme.primary
            )
        }
    }
}
