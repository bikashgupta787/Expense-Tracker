package com.example.expensetracker

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactionsDesc(): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY date ASC")
    fun getAllTransactionsAsc(): LiveData<List<Transaction>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'Income'")
    fun getTotalIncome(): LiveData<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'Expense'")
    fun getTotalExpense(): LiveData<Double?>

    // returns only categories which have expense entries
    @Query("""
        SELECT category AS category, 
               SUM(amount) AS total 
        FROM transactions 
        WHERE type = 'Expense' 
        GROUP BY category
    """)
    fun getExpenseTotalByCategory(): LiveData<List<com.example.expensetracker.CategoryExpense>>

    // Sum spent for a given category (only expenses)
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'Expense' AND category = :category")
    fun getTotalSpentForCategory(category: String): LiveData<Double?>

    // --- budget CRUD ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBudget(budget: CategoryBudget)

    @Query("SELECT * FROM category_budgets")
    fun getAllBudgets(): LiveData<List<CategoryBudget>>

    @Query("SELECT amount FROM category_budgets WHERE category = :category LIMIT 1")
    fun getBudgetAmountForCategory(category: String): LiveData<Double?>

    // Get all budgets for a specific month
    @Query("SELECT * FROM category_budgets WHERE yearMonth = :yearMonth")
    fun getBudgetsForMonth(yearMonth: String): LiveData<List<CategoryBudget>>

    // Sum spent for category in given date range (for month)
    @Query("""
        SELECT SUM(amount) FROM transactions
        WHERE type = 'Expense' AND category = :category
        AND date >= :startMs AND date <= :endMs
    """)
    fun getTotalSpentForCategoryInRange(category: String, startMs: Long, endMs: Long): LiveData<Double?>

    // Grouped sums for all categories within the month (optional for speed)
    @Query("""
        SELECT SUM(amount) FROM transactions
        WHERE type = 'Expense'
          AND date BETWEEN :startMs AND :endMs
    """)
    fun getTotalExpenseInRange(startMs: Long, endMs: Long): LiveData<Double?>

    @Query("""
        SELECT SUM(amount) FROM transactions
        WHERE type = 'Income'
          AND date BETWEEN :startMs AND :endMs
    """)
    fun getTotalIncomeInRange(startMs: Long, endMs: Long): LiveData<Double?>

    // total spent in range (startMs..endMs)
    @Query("""
    SELECT SUM(amount) FROM transactions
    WHERE type = 'Expense' AND date BETWEEN :startMs AND :endMs
""")
    fun getTotalSpentInRange(startMs: Long, endMs: Long): LiveData<Double?>

    // sum of budgets for given month
    @Query("SELECT SUM(amount) FROM category_budgets WHERE yearMonth = :yearMonth")
    fun getTotalBudgetForMonth(yearMonth: String): LiveData<Double?>

    @Query("""
    SELECT * FROM transactions
    WHERE date BETWEEN :startMs AND :endMs
    ORDER BY date DESC
""")
    fun getTransactionsInRange(startMs: Long, endMs: Long): LiveData<List<Transaction>>


}