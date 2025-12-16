package com.example.expensetracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
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

    private val repository: TransactionRepo
    private val categories: List<String>

    // selected month string "yyyy-MM"
    private val _selectedYearMonth = MutableLiveData(currentYearMonth())
    val selectedYearMonth: LiveData<String> = _selectedYearMonth

    // Combined LiveData for UI
    private val _budgetUiList = MediatorLiveData<List<BudgetUi>>()
    val budgetUiList: LiveData<List<BudgetUi>> = _budgetUiList

    // Totals LiveData
    val totalBudgetForMonth: LiveData<Double?>
    val totalSpentForMonth: LiveData<Double?>

    // Computed LiveData for UI
    val availableForMonth: LiveData<Double>
    val percentUsedForMonth: LiveData<Int>

    init {
        val dao = AppDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepo(dao)

        // Load categories from resources (string-array)
        categories = application.resources.getStringArray(R.array.expense_categories).toList()

        // Observe budgets and expenses grouped list and recompute UI list whenever they change
        _budgetUiList.addSource(repository.allBudgets) { recompute() }
        _budgetUiList.addSource(repository.expenseByCategory) { recompute() }

        // initial compute
        recompute()

        // compute start/end millis helper
        fun monthRange(ym: String) = monthRangeMillis(ym) // (startMs, endMs)

        // Map selected month -> totalBudget LiveData
        totalBudgetForMonth = _selectedYearMonth.switchMap { ym ->
            repository.getTotalBudgetForMonth(ym)
        }

        totalSpentForMonth = _selectedYearMonth.switchMap { ym ->
            val (start, end) = monthRange(ym)
            repository.getTotalSpentInRange(start, end)
        }

        // available = (budget ?: 0.0) - (spent ?: 0.0)
        availableForMonth = MediatorLiveData<Double>().apply {
            var b = totalBudgetForMonth.value ?: 0.0
            var s = totalSpentForMonth.value ?: 0.0

            fun update() { value = (b - s).coerceAtLeast(0.0) }

            addSource(totalBudgetForMonth) {
                b = it ?: 0.0
                update()
            }
            addSource(totalSpentForMonth) {
                s = it ?: 0.0
                update()
            }
        }

        // percent used = (spent / budget) * 100 (0..100)
        percentUsedForMonth = MediatorLiveData<Int>().apply {
            var b = totalBudgetForMonth.value ?: 0.0
            var s = totalSpentForMonth.value ?: 0.0

            fun update() {
                val percent = if (b > 0.0) ((s / b) * 100.0).coerceIn(0.0, 100.0) else 0.0
                value = percent.toInt()
            }

            addSource(totalBudgetForMonth) {
                b = it ?: 0.0
                update()
            }
            addSource(totalSpentForMonth) {
                s = it ?: 0.0
                update()
            }
        }

    }

    private fun recompute() {
        // get current snapshots
        val ym = selectedYearMonth.value ?: currentYearMonth()
        val budgets = repository.allBudgets.value?.filter { it.yearMonth == ym } ?: emptyList()

        val expenseByCat = repository.expenseByCategory.value ?: emptyList()

        val budgetMap = budgets.associateBy { it.category } // category -> CategoryBudget
        val expenseMap = expenseByCat.associateBy { it.category } // category -> CategoryExpense

        val uiList = categories.map { cat ->
            val budgetAmount = budgetMap[cat]?.amount
            val spent = expenseMap[cat]?.total ?: 0.0
            BudgetUi(category = cat, budgetAmount = budgetAmount, spentAmount = spent)
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
        val yearMonth = selectedYearMonth.value ?: currentYearMonth()
        val budget = CategoryBudget(
            category = category,
            yearMonth = yearMonth,
            amount = amount
        )
        repository.upsertBudget(budget)
    }

}