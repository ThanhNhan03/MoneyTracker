package com.example.moneytracker.util

import java.text.NumberFormat
import java.util.*

fun Double.toVND(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return format.format(this)
}

fun Int.toVND(): String {
    return this.toDouble().toVND()
}

fun Long.toVND(): String {
    return this.toDouble().toVND()
} 