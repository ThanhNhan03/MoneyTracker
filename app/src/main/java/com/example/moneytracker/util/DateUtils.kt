package com.example.moneytracker.util

import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

object DateUtils {
    private val vietnameseLocale = Locale("vi", "VN")
    
    // Formatters for different date patterns
    val dayMonthYearFormatter: SimpleDateFormat = SimpleDateFormat("dd MMMM yyyy", vietnameseLocale)
    val shortDateFormatter: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy", vietnameseLocale)
    val monthYearFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", vietnameseLocale)
    val shortMonthDateFormatter: SimpleDateFormat = SimpleDateFormat("dd MMM", vietnameseLocale)
    
    // Custom Vietnamese month names if needed
    private val vietnameseMonths = arrayOf(
        "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
        "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
    )
    
    private val vietnameseShortMonths = arrayOf(
        "Th1", "Th2", "Th3", "Th4", "Th5", "Th6",
        "Th7", "Th8", "Th9", "Th10", "Th11", "Th12"
    )
      // Function to format date with custom Vietnamese format
    fun formatDateVietnamese(date: Date): String {
        val today = Calendar.getInstance()
        val dateCalendar = Calendar.getInstance().apply { time = date }
        
        // Check if it's today
        if (today.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == dateCalendar.get(Calendar.DAY_OF_YEAR)) {
            return "Hôm nay"
        }
        
        // Check if it's yesterday
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, -1)
        }
        if (yesterday.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR) &&
            yesterday.get(Calendar.DAY_OF_YEAR) == dateCalendar.get(Calendar.DAY_OF_YEAR)) {
            return "Hôm qua"
        }
        
        val day = dateCalendar.get(Calendar.DAY_OF_MONTH)
        val month = dateCalendar.get(Calendar.MONTH)
        val year = dateCalendar.get(Calendar.YEAR)
        
        return "$day ${vietnameseMonths[month]} $year"
    }
      fun formatShortDateVietnamese(date: Date): String {
        val today = Calendar.getInstance()
        val dateCalendar = Calendar.getInstance().apply { time = date }
        
        // Check if it's today
        if (today.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == dateCalendar.get(Calendar.DAY_OF_YEAR)) {
            return "Hôm nay"
        }
        
            // Check if it's yesterday
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, -1)
        }
        if (yesterday.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR) &&
            yesterday.get(Calendar.DAY_OF_YEAR) == dateCalendar.get(Calendar.DAY_OF_YEAR)) {
            return "Hôm qua"
        }
        
        val day = dateCalendar.get(Calendar.DAY_OF_MONTH)
        val month = dateCalendar.get(Calendar.MONTH)
        
        return "$day ${vietnameseShortMonths[month]}"
    }
}
