package com.example.expensetracker.Database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "category_budgets",
    indices = [Index(value = ["category"], unique = true)]
)
data class CategoryBudget(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val amount: Double
)
