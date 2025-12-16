package com.example.expensetracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_budgets", primaryKeys = ["category", "yearMonth"])
data class CategoryBudget(
    val category: String,
    val yearMonth: String,
    val amount: Double // budget amount (0.0 means not set)
)
