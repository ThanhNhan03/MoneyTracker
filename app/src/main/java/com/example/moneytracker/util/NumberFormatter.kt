package com.example.moneytracker.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

object NumberFormatter {
    
    private val formatter = DecimalFormat("#,###", DecimalFormatSymbols(Locale.US))
    
    /**
     * Formats a number string with thousands separators using commas
     * Example: "1234567" -> "1,234,567"
     */
    fun formatNumber(input: String): String {
        if (input.isBlank()) return ""
        
        // Remove all non-digit characters
        val cleanInput = input.replace(Regex("[^\\d]"), "")
        
        if (cleanInput.isEmpty()) return ""
        
        return try {
            val number = cleanInput.toLong()
            formatter.format(number)
        } catch (e: NumberFormatException) {
            cleanInput
        }
    }
    
    /**
     * Removes formatting from a number string to get raw number
     * Example: "1,234,567" -> "1234567"
     */
    fun unformatNumber(formatted: String): String {
        return formatted.replace(",", "")
    }
    
    /**
     * Validates if a string can be converted to a valid number
     */
    fun isValidNumber(input: String): Boolean {
        val clean = unformatNumber(input)
        return try {
            clean.toDouble()
            clean.isNotEmpty()
        } catch (e: NumberFormatException) {
            false
        }
    }
    
    /**
     * Gets the double value from a formatted string
     */
    fun getDoubleValue(formatted: String): Double? {
        val clean = unformatNumber(formatted)
        return try {
            if (clean.isEmpty()) null else clean.toDouble()
        } catch (e: NumberFormatException) {
            null
        }
    }
}
