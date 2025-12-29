package com.example.expensetracker.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.Database.AppDatabase
import com.example.expensetracker.Database.CategoryBudget
import com.example.expensetracker.R
import com.example.expensetracker.Repository.TransactionRepo
import com.example.expensetracker.data_class.CategoryExpense
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

data class BudgetUi(
    val category: String,
    val budgetAmount: Double?, // null => not set
    val spentAmount: Double,   // 0.0 if none
) {
    val availableAmount: Double
        get() = (budgetAmount ?: 0.0) - spentAmount
    val isSet: Boolean
        get() = (budgetAmount ?: 0.0) > 0.0
}

class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val categories: List<String>

    val progressData: MediatorLiveData<Pair<Double, Double>>



    private val repository: TransactionRepo by lazy {
        val dao = AppDatabase.getDatabase(getApplication()).transactionDao()
        TransactionRepo(dao)
    }

    private val _selectedYearMonth = MutableLiveData(currentYearMonth())
    val selectedYearMonth: LiveData<String> = _selectedYearMonth

    private val expenseByCategoryForMonth: LiveData<List<CategoryExpense>> =
        selectedYearMonth.switchMap { ym ->
            val (start, end) = monthRangeMillis(ym)
            repository.getExpenseByCategoryInRange(start, end)
        }

    private val _budgetUiList = MediatorLiveData<List<BudgetUi>>()
    val budgetUiList: LiveData<List<BudgetUi>> = _budgetUiList



    // Totals LiveData
    //val totalBudgetForMonth: LiveData<Double?>
    val totalSpentForMonth: LiveData<Double?>

    // Computed LiveData for UI
    val availableForMonth: LiveData<Double>
    val percentUsedForMonth: LiveData<Int>

    init {

        categories = application.resources
            .getStringArray(R.array.expense_categories)
            .toList()

        // ---- Totals ----
        val totalBudget: LiveData<Double?> = repository.getTotalBudget()


        totalSpentForMonth = _selectedYearMonth.switchMap { ym ->
            val (start, end) = monthRangeMillis(ym)
            repository.getTotalSpentInRange(start, end)
        }

        // ---- Progress Data (AFTER totals exist) ----
        progressData = MediatorLiveData<Pair<Double, Double>>().apply {
            var budget = 0.0
            var spent = 0.0

            fun update() {
                value = budget to spent
            }

            addSource(totalBudget) {
                budget = it ?: 0.0
                update()
            }

            addSource(totalSpentForMonth) {
                spent = it ?: 0.0
                update()
            }
        }

        // ---- Available ----
        availableForMonth = MediatorLiveData<Double>().apply {
            var b = 0.0
            var s = 0.0

            fun update() { value = (b - s).coerceAtLeast(0.0) }

            addSource(totalBudget) {
                b = it ?: 0.0
                update()
            }
            addSource(totalSpentForMonth) {
                s = it ?: 0.0
                update()
            }
        }

        // ---- Percent ----
        percentUsedForMonth = MediatorLiveData<Int>().apply {
            var b = 0.0
            var s = 0.0

            fun update() {
                value = if (b > 0.0)
                    ((s / b) * 100).toInt().coerceIn(0, 100)
                else 0
            }

            addSource(totalBudget) {
                b = it ?: 0.0
                update()
            }
            addSource(totalSpentForMonth) {
                s = it ?: 0.0
                update()
            }
        }

        // ---- UI recompute ----
        _budgetUiList.addSource(repository.allBudgets) { recompute() }
        _budgetUiList.addSource(expenseByCategoryForMonth) { recompute() }
        //_budgetUiList.addSource(selectedYearMonth) { recompute() }

        recompute()
    }


    private fun recompute() {
        val budgets = repository.allBudgets.value ?: emptyList()
        val expenses = expenseByCategoryForMonth.value ?: emptyList()

        val budgetMap = budgets.associateBy { it.category }
        val expenseMap = expenses.associateBy { it.category }

        val uiList = categories.map { category ->
            val budgetAmount = budgetMap[category]?.amount
            val spentAmount = expenseMap[category]?.total ?: 0.0

            BudgetUi(
                category = category,
                budgetAmount = budgetAmount,
                spentAmount = spentAmount
            )
        }

        _budgetUiList.value = uiList
    }



    fun setSelectedYearMonth(ym: String) {
        _selectedYearMonth.value = ym
    }


    private fun monthRangeMillis(yearMonth: String): Pair<Long, Long> {
        val parts = yearMonth.split("-")
        val year = parts[0].toInt()
        val month = parts[1].toInt() - 1
        val cal = Calendar.getInstance()
        cal.set(year, month, 1, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        cal.add(Calendar.MILLISECOND, -1)
        val end = cal.timeInMillis
        return start to end
    }

    fun currentYearMonth(): String {
        val c = Calendar.getInstance()
        val y = c.get(Calendar.YEAR)
        val m = c.get(Calendar.MONTH) + 1
        return String.format(Locale.getDefault(), "%04d-%02d", y, m)
    }

    fun setBudget(category: String, amount: Double) = viewModelScope.launch {
        val budget = CategoryBudget(
            category = category,
            amount = amount
        )
        repository.upsertBudget(budget)
    }


}