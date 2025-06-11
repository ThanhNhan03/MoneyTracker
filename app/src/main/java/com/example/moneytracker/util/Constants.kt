package com.example.moneytracker.util

import java.text.NumberFormat
import java.util.*

object Constants {
    const val DATABASE_NAME = "money_tracker_db"
    const val TRANSACTION_TYPE_INCOME = "income"
    const val TRANSACTION_TYPE_EXPENSE = "expense"
    
    // Date formats
    const val DATE_FORMAT = "dd/MM/yyyy"
    const val DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm"
    const val MONTH_YEAR_FORMAT = "MM/yyyy"
    const val DAY_MONTH_FORMAT = "dd MMM"
    
    // Default categories
    val DEFAULT_INCOME_CATEGORIES = listOf(
        "Salary",
        "Freelance",
        "Investment",
        "Gift",
        "Other Income"
    )
    
    val DEFAULT_EXPENSE_CATEGORIES = listOf(
        "Food & Drinks",
        "Shopping",
        "Transportation",
        "Housing",
        "Bills",
        "Entertainment",
        "Health",
        "Education",
        "Travel",
        "Other Expense"
    )
}

// Extension function to format currency
fun Double.toCurrencyFormat(currencyCode: String = "VND"): String {
    val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    format.currency = Currency.getInstance(currencyCode)
    return format.format(this)
}

// Extension function to parse currency string to double
fun String.parseCurrency(): Double {
    return try {
        this.replace("[^0-9.-]+".toRegex(), "").toDouble()
    } catch (e: NumberFormatException) {
        0.0
    }
}

// Extension function to format date
fun Date.formatDate(format: String = Constants.DATE_FORMAT): String {
    val sdf = java.text.SimpleDateFormat(format, Locale.getDefault())
    return sdf.format(this)
}

// Extension function to get start of day
fun Date.startOfDay(): Date {
    val calendar = Calendar.getInstance().apply {
        time = this@startOfDay
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.time
}

// Extension function to get end of day
fun Date.endOfDay(): Date {
    val calendar = Calendar.getInstance().apply {
        time = this@endOfDay
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }
    return calendar.time
}

// Extension function to get start of month
fun Date.startOfMonth(): Date {
    val calendar = Calendar.getInstance().apply {
        time = this@startOfMonth
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.time
}

// Extension function to get end of month
fun Date.endOfMonth(): Date {
    val calendar = Calendar.getInstance().apply {
        time = this@endOfMonth
        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }
    return calendar.time
}
