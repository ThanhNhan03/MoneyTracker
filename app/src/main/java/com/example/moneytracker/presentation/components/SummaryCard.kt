package com.example.moneytracker.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moneytracker.data.local.entities.Transaction
import com.example.moneytracker.util.toVND
import com.example.moneytracker.ui.theme.*

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
                        color = IncomeGreen,
                        isPositive = true
                    )
                }
                "expense" -> {
                    SummaryItem(
                        label = "Chi tiêu",
                        amount = totalExpense,
                        color = ExpenseRed,
                        isPositive = false
                    )
                }
                else -> {
                    SummaryItem(
                        label = "Thu nhập",
                        amount = totalIncome,
                        color = IncomeGreen,
                        isPositive = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SummaryItem(
                        label = "Chi tiêu",
                        amount = totalExpense,
                        color = ExpenseRed,
                        isPositive = false
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    SummaryItem(
                        label = "Số dư ròng",
                        amount = netAmount,
                        color = if (netAmount >= 0) IncomeGreen else ExpenseRed,
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
