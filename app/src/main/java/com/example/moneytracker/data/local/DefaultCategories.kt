package com.example.moneytracker.data.local

import com.example.moneytracker.data.local.entities.Category

object DefaultCategories {
    val expenseCategories = listOf(
        Category(name = "Ăn uống", type = "expense", icon = "restaurant", isDefault = true),
        Category(name = "Di chuyển", type = "expense", icon = "directions_car", isDefault = true),
        Category(name = "Mua sắm", type = "expense", icon = "shopping_cart", isDefault = true),
        Category(name = "Giải trí", type = "expense", icon = "movie", isDefault = true),
        Category(name = "Hóa đơn", type = "expense", icon = "receipt", isDefault = true),
        Category(name = "Y tế", type = "expense", icon = "local_hospital", isDefault = true),
        Category(name = "Giáo dục", type = "expense", icon = "school", isDefault = true),
        Category(name = "Du lịch", type = "expense", icon = "flight", isDefault = true),
        Category(name = "Quà tặng", type = "expense", icon = "card_giftcard", isDefault = true),
        Category(name = "Khác", type = "expense", icon = "more_horiz", isDefault = true)
    )

    val incomeCategories = listOf(
        Category(name = "Lương", type = "income", icon = "work", isDefault = true),
        Category(name = "Thưởng", type = "income", icon = "stars", isDefault = true),
        Category(name = "Đầu tư", type = "income", icon = "trending_up", isDefault = true),
        Category(name = "Quà tặng", type = "income", icon = "card_giftcard", isDefault = true),
        Category(name = "Khác", type = "income", icon = "more_horiz", isDefault = true)
    )

    val allCategories = expenseCategories + incomeCategories
}