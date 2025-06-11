package com.example.moneytracker.data.local

import com.example.moneytracker.data.local.entities.Category

object DefaultCategories {
    val expenseCategories = listOf(
        Category(name = "Ăn uống", type = "expense", icon = "restaurant"),
        Category(name = "Di chuyển", type = "expense", icon = "directions_car"),
        Category(name = "Mua sắm", type = "expense", icon = "shopping_cart"),
        Category(name = "Giải trí", type = "expense", icon = "movie"),
        Category(name = "Hóa đơn", type = "expense", icon = "receipt"),
        Category(name = "Y tế", type = "expense", icon = "local_hospital"),
        Category(name = "Giáo dục", type = "expense", icon = "school"),
        Category(name = "Du lịch", type = "expense", icon = "flight"),
        Category(name = "Quà tặng", type = "expense", icon = "card_giftcard"),
        Category(name = "Khác", type = "expense", icon = "more_horiz")
    )

    val incomeCategories = listOf(
        Category(name = "Lương", type = "income", icon = "work"),
        Category(name = "Thưởng", type = "income", icon = "stars"),
        Category(name = "Đầu tư", type = "income", icon = "trending_up"),
        Category(name = "Quà tặng", type = "income", icon = "card_giftcard"),
        Category(name = "Khác", type = "income", icon = "more_horiz")
    )

    val allCategories = expenseCategories + incomeCategories
} 