package com.example.moneytracker.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.data.local.entities.Category
import com.example.moneytracker.data.local.entities.Transaction
import com.example.moneytracker.util.toVND
import kotlin.math.cos
import kotlin.math.sin

data class CategoryExpense(
    val category: Category,
    val amount: Double,
    val percentage: Float,
    val color: Color
)

@Composable
fun ExpenseByCategoryChart(
    transactions: List<Transaction>,
    categories: List<Category>,
    modifier: Modifier = Modifier
) {
    val expenseTransactions = transactions.filter { it.type == "expense" }
    val totalExpense = expenseTransactions.sumOf { it.amount }
    
    if (totalExpense <= 0 || expenseTransactions.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Không có dữ liệu chi tiêu",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }
    
    // Group expenses by category
    val categoryExpenses = expenseTransactions
        .groupBy { it.categoryId }
        .map { (categoryId, transactions) ->
            val category = categories.find { it.id == categoryId }
                ?: Category(id = categoryId, name = "Khác", type = "expense")
            val amount = transactions.sumOf { it.amount }
            val percentage = (amount / totalExpense * 100).toFloat()
            
            CategoryExpense(
                category = category,
                amount = amount,
                percentage = percentage,
                color = getCategoryColor(categoryId.toInt())
            )
        }
        .sortedByDescending { it.amount }
    
    var selectedIndex by remember { mutableStateOf(-1) }
    
    Column(modifier = modifier) {
        // Pie Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            PieChart(
                data = categoryExpenses,
                selectedIndex = selectedIndex,
                onSliceClick = { index -> selectedIndex = if (selectedIndex == index) -1 else index },
                modifier = Modifier.size(180.dp)
            )
            
            // Center text showing total
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tổng chi tiêu",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = totalExpense.toVND(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Legend
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categoryExpenses.forEachIndexed { index, item ->
                CategoryLegendItem(
                    categoryExpense = item,
                    isSelected = selectedIndex == index,
                    onClick = { selectedIndex = if (selectedIndex == index) -1 else index }
                )
            }
        }
    }
}

@Composable
private fun PieChart(
    data: List<CategoryExpense>,
    selectedIndex: Int,
    onSliceClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        label = "pie_chart_animation"
    )
    
    Canvas(
        modifier = modifier.clickable { /* Handle clicks in drawScope */ }
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f * 0.8f
        val selectedRadius = radius * 1.1f
        
        var startAngle = -90f
        
        data.forEachIndexed { index, item ->
            val sweepAngle = (item.percentage / 100f) * 360f * animatedProgress
            val currentRadius = if (selectedIndex == index) selectedRadius else radius
            
            drawArc(
                color = item.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(
                    center.x - currentRadius,
                    center.y - currentRadius
                ),
                size = Size(currentRadius * 2, currentRadius * 2)
            )
            
            startAngle += sweepAngle
        }
        
        // Draw center circle for donut effect
        drawCircle(
            color = Color.White,
            radius = radius * 0.4f,
            center = center
        )
    }
}

@Composable
private fun CategoryLegendItem(
    categoryExpense: CategoryExpense,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color indicator
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(categoryExpense.color)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Category name
            Text(
                text = categoryExpense.category.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                modifier = Modifier.weight(1f)
            )
            
            // Percentage and amount
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${categoryExpense.percentage.toInt()}%",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = categoryExpense.amount.toVND(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun getCategoryColor(categoryId: Int): Color {
    val colors = listOf(
        Color(0xFF4285F4), // Blue
        Color(0xFFEA4335), // Red
        Color(0xFFFBBC05), // Yellow
        Color(0xFF34A853), // Green
        Color(0xFF9C27B0), // Purple
        Color(0xFFFF6D00), // Orange
        Color(0xFF00BCD4), // Cyan
        Color(0xFF795548), // Brown
        Color(0xFF607D8B), // Blue Grey
        Color(0xFFE91E63)  // Pink
    )
    return colors[categoryId % colors.size]
}
