package com.example.moneytracker.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionItemCard(
    transaction: Transaction,
    categories: List<Category>,
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
                        IncomeGreen.copy(alpha = 0.2f) 
                    else 
                        ExpenseRed.copy(alpha = 0.2f)
                ) {
                    Icon(
                        imageVector = if (transaction.type == "income") 
                            Icons.Default.TrendingUp 
                        else 
                            Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (transaction.type == "income") 
                            IncomeGreen 
                        else 
                            ExpenseRed,
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
                    IncomeGreen 
                else 
                    ExpenseRed
            )
        }
    }
}
