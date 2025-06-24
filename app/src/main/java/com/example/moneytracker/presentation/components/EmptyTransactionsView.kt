package com.example.moneytracker.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EmptyTransactionsView(
    selectedFilter: String,
    selectedDateRange: String = "all",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
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
                text = when {
                    selectedDateRange == "today" -> "Không có giao dịch nào hôm nay"
                    selectedDateRange == "this_week" -> "Không có giao dịch nào tuần này"
                    selectedDateRange == "this_month" -> "Không có giao dịch nào tháng này"
                    selectedDateRange == "last_month" -> "Không có giao dịch nào tháng trước"
                    selectedFilter == "income" -> "Chưa có giao dịch thu nhập"
                    selectedFilter == "expense" -> "Chưa có giao dịch chi tiêu"
                    else -> "Chưa có giao dịch nào"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
