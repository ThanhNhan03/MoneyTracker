package com.example.moneytracker.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Extension functions and utilities for theme colors
 */

/**
 * Get a category color by index
 * Useful for consistent category coloring throughout the app
 * Now using vibrant neon colors
 */
fun getCategoryColor(index: Int): Color {
    val categoryColors = listOf(
        CategoryColor1, CategoryColor2, CategoryColor3, CategoryColor4,
        CategoryColor5, CategoryColor6, CategoryColor7, CategoryColor8,
        CategoryColor9, CategoryColor10, CategoryColor11, CategoryColor12
    )
    return categoryColors[index % categoryColors.size]
}

/**
 * Get gradient colors for balance cards - Neon style
 */
data class GradientColors(val start: Color, val end: Color)

fun getBalanceGradient(): GradientColors {
    return GradientColors(GradientStart, GradientEnd) // Purple to Cyan neon
}

/**
 * Get status color based on amount (positive/negative)
 * Using neon cyan for income and pink-red for expense
 */
fun getAmountColor(amount: Double): Color {
    return if (amount >= 0) IncomeGreen else ExpenseRed // Cyan neon vs Pink red
}

/**
 * Get surface color with alpha for cards
 */
fun getSurfaceColorWithAlpha(color: Color, alpha: Float = 0.1f): Color {
    return color.copy(alpha = alpha)
}
