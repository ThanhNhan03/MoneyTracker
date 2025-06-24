package com.example.moneytracker.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*

data class DateRange(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val label: String
)

@Composable
fun DateRangeFilter(
    selectedRange: String,
    customStartDate: LocalDate?,
    customEndDate: LocalDate?,
    onRangeSelected: (String) -> Unit,
    onCustomDateSelected: (LocalDate?, LocalDate?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDropdown by remember { mutableStateOf(false) }
    
    val dateRanges = remember {
        val today = LocalDate.now()
        val startOfMonth = today.withDayOfMonth(1)
        val endOfMonth = YearMonth.from(today).atEndOfMonth()
        val startOfLastMonth = startOfMonth.minusMonths(1)
        val endOfLastMonth = YearMonth.from(startOfLastMonth).atEndOfMonth()
        
        listOf(
            "all" to "Tất cả",
            "today" to "Hôm nay",
            "this_week" to "Tuần này",
            "this_month" to "Tháng này",
            "last_month" to "Tháng trước",
            "custom" to "Tùy chọn"
        )
    }
    
    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { showDropdown = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = dateRanges.find { it.first == selectedRange }?.second ?: "Tất cả",
                modifier = Modifier.weight(1f)
            )
        }
        
        DropdownMenu(
            expanded = showDropdown,
            onDismissRequest = { showDropdown = false }
        ) {
            dateRanges.forEach { (key, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onRangeSelected(key)
                        showDropdown = false
                    },
                    leadingIcon = {
                        if (selectedRange == key) {
                            Icon(Icons.Default.Check, contentDescription = null)
                        }
                    }
                )
            }
        }
    }
    
    // Custom date selection
    if (selectedRange == "custom") {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Từ ngày:",
                    style = MaterialTheme.typography.bodySmall
                )
                DateSelector(
                    selectedDate = customStartDate ?: LocalDate.now(),
                    onDateSelected = { date ->
                        onCustomDateSelected(date, customEndDate)
                    },
                    showMonthYear = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Đến ngày:",
                    style = MaterialTheme.typography.bodySmall
                )
                DateSelector(
                    selectedDate = customEndDate ?: LocalDate.now(),
                    onDateSelected = { date ->
                        onCustomDateSelected(customStartDate, date)
                    },
                    showMonthYear = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
