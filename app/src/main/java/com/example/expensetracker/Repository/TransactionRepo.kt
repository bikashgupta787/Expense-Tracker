package com.example.expensetracker.Repository

import androidx.lifecycle.LiveData
import com.example.expensetracker.Database.CategoryBudget
import com.example.expensetracker.Database.TransactionDao
import com.example.expensetracker.data_class.CategoryExpense
import com.example.expensetracker.data_class.Transaction

class TransactionRepo(private val dao: TransactionDao) {

    val transactionDesc: LiveData<List<Transaction>> = dao.getAllTransactionsDesc()
    val transactionAsc: LiveData<List<Transaction>> = dao.getAllTransactionsAsc()

    val totalIncome: LiveData<Double?> = dao.getTotalIncome()
    val totalExpense: LiveData<Double?> = dao.getTotalExpense()

    val expenseByCategory: LiveData<List<CategoryExpense>> = dao.getExpenseTotalByCategory()

    //val allBudgets: LiveData<List<CategoryBudget>> = dao.getAllBudgets()
    val allBudgets = dao.getAllBudgets()

    fun getTotalBudget(): LiveData<Double?> {
        return dao.getTotalBudget()
    }

    fun getTotalExpenseInRange(startMs: Long, endMs: Long): LiveData<Double?> {
        return dao.getTotalExpenseInRange(startMs, endMs)
    }

    fun getTotalIncomeInRange(startMs: Long, endMs: Long): LiveData<Double?> {
        return dao.getTotalIncomeInRange(startMs, endMs)
    }


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

    fun getExpenseByCategoryInRange(
        startMs: Long,
        endMs: Long
    ): LiveData<List<CategoryExpense>> {
        return dao.getExpenseByCategoryInRange(startMs, endMs)
    }

}