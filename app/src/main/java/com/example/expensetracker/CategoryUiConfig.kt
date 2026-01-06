package com.example.expensetracker

object CategoryUiConfig {

    fun iconRes(category: String): Int {
        return when (category.lowercase()) {
            "food" -> R.drawable.ic_food
            "shopping" -> R.drawable.ic_shopping
            "travel" -> R.drawable.ic_transport
            "health" -> R.drawable.ic_health
            "office" -> R.drawable.ic_work
            "bills" -> R.drawable.ic_wallet
            "entertainment" -> R.drawable.ic_entertainment
            "salary" -> R.drawable.ic_work
            "groceries" -> R.drawable.ic_groceries
            "bonus" -> R.drawable.ic_bonus
            "extra income" -> R.drawable.ic_extra_income
            "education" -> R.drawable.ic_education
            else -> R.drawable.ic_category
        }
    }

    fun colorRes(category: String): Int {
        return when (category.lowercase()) {
            "food" -> R.color.cat_food
            "shopping" -> R.color.cat_shopping
            "travel" -> R.color.cat_transport
            "health" -> R.color.cat_health
            "office" -> R.color.cat_office
            "bills" -> R.color.cat_bills
            "entertainment" -> R.color.cat_entertainment
            "salary" -> R.color.cat_salary
            "groceries" -> R.color.cat_grocery
            "bonus" -> R.color.cat_bonus
            "extra income" -> R.color.cat_income
            "education" -> R.color.cat_education
            else -> R.color.cat_other
        }
    }
}