package com.example.expensetracker.Database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

//@Entity(tableName = "category_budgets", primaryKeys = ["category", "yearMonth"])
//data class CategoryBudget(
//    val category: String,
//    val yearMonth: String,
//    val amount: Double // budget amount (0.0 means not set)
//)

@Entity(
    tableName = "category_budgets",
    indices = [Index(value = ["category"], unique = true)]
)
data class CategoryBudget(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val amount: Double
)
