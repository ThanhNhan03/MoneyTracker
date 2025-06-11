package com.example.moneytracker.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
fun TransactionChart(
    modifier: Modifier = Modifier,
    data: Map<String, Double> = mapOf(
        "Food" to 35.0,
        "Shopping" to 25.0,
        "Transport" to 20.0,
        "Bills" to 15.0,
        "Other" to 5.0
    ),
    colors: List<Color> = listOf(
        Color(0xFF4285F4), // Blue
        Color(0xFFEA4335), // Red
        Color(0xFFFBBC05), // Yellow
        Color(0xFF34A853), // Green
        Color(0xFF9C27B0)  // Purple
    )
) {
    val total = data.values.sum()
    val proportions = data.mapValues { it.value.toFloat() / total.toFloat() }
    
    var selectedIndex by remember { mutableStateOf(-1) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (selectedIndex >= 0) 1f else 0f,
        label = "chart_animation"
    )
    
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val size = min(constraints.maxWidth, constraints.maxHeight).toFloat()
        val outerRadius = size / 2f
        val innerRadius = outerRadius * 0.4f
        
        // Get the surface color before entering Canvas
        val surfaceColor = MaterialTheme.colorScheme.surface
        
        // Draw the pie chart
        Canvas(
            modifier = Modifier
                .size(size.dp)
                .padding(8.dp)
        ) {
            // Draw the pie segments
            var startAngle = -90f
            
            proportions.values.forEachIndexed { index, proportion ->
                val sweepAngle = 360f * proportion
                
                // Draw the segment
                drawPieSegment(
                    color = colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    radius = outerRadius,
                    isSelected = selectedIndex == index,
                    animatedProgress = animatedProgress
                )
                
                startAngle += sweepAngle
            }
            
            // Draw the center hole using the pre-fetched surface color
            drawCircle(
                color = surfaceColor,
                radius = innerRadius
            )
        }
        
        // Center text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (selectedIndex >= 0) {
                    data.keys.elementAt(selectedIndex)
                } else "Total",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            
            if (selectedIndex >= 0) {
                val percentage = (proportions.values.elementAt(selectedIndex) * 100f).toInt()
                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors[selectedIndex % colors.size],
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = data.size.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "categories",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // Legend
        val legendSpacing = 4.dp
        val legendItemHeight = 16.dp
        val legendItemWidth = with(LocalDensity.current) { (size * 0.6f).toDp() }
        
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        ) {
            data.keys.take(3).forEachIndexed { index, label ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(vertical = legendSpacing / 2)
                        .height(legendItemHeight)
                ) {
                    Box(
                        modifier = Modifier
                            .size(legendItemHeight)
                            .clip(MaterialTheme.shapes.small)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(colors[index % colors.size])
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${(proportions.values.elementAt(index) * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (data.size > 3) {
                Text(
                    text = "+${data.size - 3} more",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

private fun DrawScope.drawPieSegment(
    color: Color,
    startAngle: Float,
    sweepAngle: Float,
    radius: Float,
    isSelected: Boolean,
    animatedProgress: Float
) {
    val selectedOffset = if (isSelected) 15f * animatedProgress else 0f
    val centerOffset = Offset(size.width / 2, size.height / 2)
    
    // Calculate the direction to offset the selected segment
    val midAngle = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
    val offsetX = (selectedOffset * kotlin.math.cos(midAngle)).toFloat()
    val offsetY = (selectedOffset * kotlin.math.sin(midAngle)).toFloat()
    
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = true,
        topLeft = Offset(
            centerOffset.x - radius + offsetX,
            centerOffset.y - radius + offsetY
        ),
        size = Size(radius * 2, radius * 2),
        alpha = if (isSelected) 1f else 0.7f
    )
}
