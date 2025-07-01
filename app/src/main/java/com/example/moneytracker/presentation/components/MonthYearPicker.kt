package com.example.moneytracker.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthYearPicker(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var showModal by remember { mutableStateOf(false) }
    
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("vi", "VN"))
    
    // Compact picker button
    TextButton(
        onClick = { showModal = true },
        modifier = modifier
    ) {
        Text(
            text = formatter.format(selectedDate).replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium
            )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = "Chọn tháng/năm"
        )
    }
    
    // Modal dialog
    if (showModal) {
        MonthYearPickerDialog(
            selectedDate = selectedDate,
            onDateSelected = { date ->
                onDateSelected(date)
                showModal = false
            },
            onDismiss = { showModal = false }
        )
    }
}

@Composable
private fun MonthYearPickerDialog(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedYear by remember { mutableStateOf(selectedDate.year) }
    var selectedMonth by remember { mutableStateOf(selectedDate.monthValue) }
    
    val months = listOf(
        "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4",
        "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", 
        "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
    )
    
    val currentYear = LocalDate.now().year
    val years = (currentYear - 10..currentYear + 5).toList()
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Chọn tháng và năm",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Month picker
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Tháng",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        val monthListState = rememberLazyListState(
                            initialFirstVisibleItemIndex = maxOf(0, selectedMonth - 3)
                        )
                        
                        LazyColumn(
                            state = monthListState,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            items(months.withIndex().toList()) { (index, month) ->
                                val monthNumber = index + 1
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .clickable { selectedMonth = monthNumber },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (monthNumber == selectedMonth) 
                                            MaterialTheme.colorScheme.primaryContainer
                                        else 
                                            MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    Text(
                                        text = month,
                                        modifier = Modifier.padding(12.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (monthNumber == selectedMonth)
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        else
                                            MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                    
                    // Year picker
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Năm",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        val yearListState = rememberLazyListState(
                            initialFirstVisibleItemIndex = maxOf(0, years.indexOf(selectedYear) - 2)
                        )
                        
                        LazyColumn(
                            state = yearListState,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            items(years) { year ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .clickable { selectedYear = year },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (year == selectedYear) 
                                            MaterialTheme.colorScheme.primaryContainer
                                        else 
                                            MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    Text(
                                        text = year.toString(),
                                        modifier = Modifier.padding(12.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (year == selectedYear)
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        else
                                            MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Hủy")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            val newDate = LocalDate.of(selectedYear, selectedMonth, 1)
                            onDateSelected(newDate)
                        }
                    ) {
                        Text("Chọn")
                    }
                }
            }
        }
    }
}
