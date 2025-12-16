package com.example.expensetracker

import androidx.lifecycle.LiveData

class TransactionRepo(private val dao:TransactionDao) {

    val transactionDesc: LiveData<List<Transaction>> = dao.getAllTransactionsDesc()
    val transactionAsc: LiveData<List<Transaction>> = dao.getAllTransactionsAsc()

    val totalIncome: LiveData<Double?> = dao.getTotalIncome()
    val totalExpense: LiveData<Double?> = dao.getTotalExpense()

    val expenseByCategory: LiveData<List<CategoryExpense>> = dao.getExpenseTotalByCategory()

    val allBudgets: LiveData<List<CategoryBudget>> = dao.getAllBudgets()

    fun getBudgetsForMonth(yearMonth: String): LiveData<List<CategoryBudget>> =
        dao.getBudgetsForMonth(yearMonth)

    fun getBudgetAmountLive(category: String): LiveData<Double?> = dao.getBudgetAmountForCategory(category)
    fun getSpentAmountLive(category: String): LiveData<Double?> = dao.getTotalSpentForCategory(category)

    fun getTotalExpenseInRange(startMs: Long, endMs: Long): LiveData<Double?> {
        return dao.getTotalExpenseInRange(startMs, endMs)
    }

    fun getTotalIncomeInRange(startMs: Long, endMs: Long): LiveData<Double?> {
        return dao.getTotalIncomeInRange(startMs, endMs)
    }


    fun getTotalBudgetForMonth(yearMonth: String): LiveData<Double?> =
        dao.getTotalBudgetForMonth(yearMonth)

    fun getTotalSpentInRange(startMs: Long, endMs: Long): LiveData<Double?> =
        dao.getTotalSpentInRange(startMs, endMs)

    suspend fun upsertBudget(budget: CategoryBudget) = dao.insertOrUpdateBudget(budget)

    suspend fun  insert(transaction: Transaction) {
        dao.insert(transaction)
    }

    suspend fun delete(transaction: Transaction) {
        dao.delete(transaction)
    }

    fun getTransactionsInRange(startMs: Long, endMs: Long): LiveData<List<Transaction>> =
        dao.getTransactionsInRange(startMs, endMs)
}