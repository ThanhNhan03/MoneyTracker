package com.example.moneytracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moneytracker.data.local.entities.Category
import com.example.moneytracker.data.local.entities.Transaction
import com.example.moneytracker.data.remote.GeminiAiService
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

data class AiInsight(
    val text: String,
    val icon: ImageVector = Icons.Default.Psychology,
    val backgroundColor: Color = Color(0xFF9C27B0).copy(alpha = 0.1f),
    val iconColor: Color = Color(0xFF9C27B0)
)

@Composable
fun FinancialInsights(
    transactions: List<Transaction>,
    categories: List<Category>,
    selectedDate: LocalDate = LocalDate.now(),
    modifier: Modifier = Modifier,
    geminiService: GeminiAiService? = null
) {
    var aiInsights by remember { mutableStateOf<List<AiInsight>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var useAi by rememberSaveable { mutableStateOf(false) }
    
    val totalIncome = transactions.filter { it.type == "income" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == "expense" }.sumOf { it.amount }
    val balance = totalIncome - totalExpense
    val topExpenseCategory = getTopExpenseCategory(transactions, categories)
    val monthYear = DateTimeFormatter.ofPattern("MM/yyyy", Locale("vi", "VN")).format(selectedDate)
    
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(transactions, selectedDate, useAi) {
        if (transactions.isNotEmpty() && geminiService != null && useAi) {
            isLoading = true
            try {
                val insights = geminiService.generateFinancialInsights(
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    balance = balance,
                    topExpenseCategory = topExpenseCategory,
                    transactionCount = transactions.size,
                    monthYear = monthYear
                )
                aiInsights = insights.map { AiInsight(text = it) }
            } catch (e: Exception) {
                aiInsights = listOf(
                    AiInsight(
                        text = "Hôm nay AI đang bận, nhưng tài chính bạn vẫn ổn mà! 🤖✨",
                        icon = Icons.Default.Psychology
                    )
                )
            } finally {
                isLoading = false
            }
        } else if (transactions.isNotEmpty() && geminiService != null && !useAi) {
            aiInsights = getBasicInsights(totalIncome, totalExpense, balance, topExpenseCategory)
        } else if (transactions.isNotEmpty()) {
            aiInsights = listOf(
                AiInsight(
                    text = "Cần kết nối AI để có phân tích chi tiết hơn!",
                    icon = Icons.Default.CloudOff,
                    iconColor = Color(0xFF757575),
                    backgroundColor = Color(0xFF757575).copy(alpha = 0.1f)
                )
            )
        }
    }
    
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (useAi && geminiService != null) "AI Phân tích tài chính" else "Phân tích tài chính",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(1f)
            )
            
            if (geminiService != null) {
                // AI toggle switch
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AI",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = useAi,
                        onCheckedChange = { useAi = it },
                        modifier = Modifier.scale(0.8f)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        when {
            isLoading -> {
                // Hiển thị loading với text vui
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2196F3).copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 3.dp,
                            color = Color(0xFF2196F3)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "AI đang suy nghĩ để tư vấn cho bạn...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color(0xFF2196F3)
                        )
                    }
                }
            }
            aiInsights.isEmpty() -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Chưa có đủ dữ liệu để phân tích",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                aiInsights.forEach { insight ->
                    AiInsightCard(insight = insight)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun AiInsightCard(insight: AiInsight) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = insight.backgroundColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(insight.iconColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = insight.icon,
                    contentDescription = null,
                    tint = insight.iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = insight.text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun getTopExpenseCategory(
    transactions: List<Transaction>,
    categories: List<Category>
): String? {
    val expenseTransactions = transactions.filter { it.type == "expense" }
    if (expenseTransactions.isEmpty()) return null
    
    val categoryExpenses = expenseTransactions
        .groupBy { it.categoryId }
        .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }
    
    val topCategoryId = categoryExpenses.maxByOrNull { it.value }?.key
    return categories.find { it.id == topCategoryId }?.name
}

private fun getBasicInsights(
    totalIncome: Double,
    totalExpense: Double,
    balance: Double,
    topExpenseCategory: String?
): List<AiInsight> {
    val insights = mutableListOf<AiInsight>()
    
    val expenseRatio = if (totalIncome > 0) totalExpense / totalIncome else 0.0
    when {
        expenseRatio > 1.0 -> insights.add(AiInsight(
            text = "Chi tiêu vượt thu nhập - cần cân đối lại ngân sách!",
            icon = Icons.Default.Warning,
            iconColor = Color(0xFFEA4335),
            backgroundColor = Color(0xFFEA4335).copy(alpha = 0.1f)
        ))
        expenseRatio > 0.8 -> insights.add(AiInsight(
            text = "Chi tiêu 80% thu nhập - hãy thận trọng với tiền bạc!",
            icon = Icons.Default.TrendingUp,
            iconColor = Color(0xFFFF9800),
            backgroundColor = Color(0xFFFF9800).copy(alpha = 0.1f)
        ))
        expenseRatio < 0.3 -> insights.add(AiInsight(
            text = "Tiết kiệm rất tốt - bạn đang quản lý tài chính xuất sắc!",
            icon = Icons.Default.Star,
            iconColor = Color(0xFF4CAF50),
            backgroundColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
        ))
        else -> insights.add(AiInsight(
            text = "Chi tiêu hợp lý - tài chính đang cân bằng tốt!",
            icon = Icons.Default.ThumbUp,
            iconColor = Color(0xFF2196F3),
            backgroundColor = Color(0xFF2196F3).copy(alpha = 0.1f)
        ))
    }
    
    // Phân tích số dư
    when {
        balance > totalIncome * 0.5 -> insights.add(AiInsight(
            text = "Số dư khá cao - có thể cân nhắc đầu tư!",
            icon = Icons.Default.AccountBalance,
            iconColor = Color(0xFF4CAF50),
            backgroundColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
        ))
        balance > 0 -> insights.add(AiInsight(
            text = "Còn dư tiền cuối tháng - quản lý tốt!",
            icon = Icons.Default.Favorite,
            iconColor = Color(0xFF4CAF50),
            backgroundColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
        ))
        balance < 0 -> insights.add(AiInsight(
            text = "Số dư âm - cần xem xét lại chi tiêu!",
            icon = Icons.Default.SentimentDissatisfied,
            iconColor = Color(0xFFEA4335),
            backgroundColor = Color(0xFFEA4335).copy(alpha = 0.1f)
        ))
    }
    
    // Phân tích danh mục chi tiêu
    topExpenseCategory?.let { category ->
        insights.add(AiInsight(
            text = "Chi nhiều nhất cho '$category' - đây là ưu tiên chính!",
            icon = Icons.Default.Category,
            iconColor = Color(0xFF9C27B0),
            backgroundColor = Color(0xFF9C27B0).copy(alpha = 0.1f)
        ))
    }
    
    return insights.take(3)
}
