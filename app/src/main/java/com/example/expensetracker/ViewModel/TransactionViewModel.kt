package com.example.expensetracker.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.switchMap
import com.example.expensetracker.Database.AppDatabase
import com.example.expensetracker.Repository.TransactionRepo
import com.example.expensetracker.data_class.CategoryExpense
import com.example.expensetracker.data_class.Transaction
import java.util.Calendar

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepo
    private val _sortNewestFirst = MutableLiveData(true)
    val sortNewestFirst: LiveData<Boolean> = _sortNewestFirst
    val expenseByCategory: LiveData<List<CategoryExpense>>
    val sortedTransactions = MediatorLiveData<List<Transaction>>()
    val totalExpenseForMonth: LiveData<Double?>
    val totalIncomeForMonth: LiveData<Double?>




    private val _selectedYearMonth = MutableLiveData(currentYearMonth())
    val selectedYearMonth: LiveData<String> = _selectedYearMonth

    val allTransactions:LiveData<List<Transaction>>
    val totalIncome: LiveData<Double?>
    val totalExpense: LiveData<Double?>
    // Exposed list of filtered transactions
    val filteredTransactions: LiveData<List<Transaction>>


    init {
        val dao = AppDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepo(dao)
        totalExpense = repository.totalExpense
        totalIncome = repository.totalIncome

        allTransactions = _sortNewestFirst.switchMap { newestFirst ->
                if (newestFirst) repository.transactionDesc else repository.transactionAsc
            }

        //expenseByCategory = repository.expenseByCategory

        expenseByCategory = selectedYearMonth.switchMap { ym ->
            val (start, end) = monthRangeMillis(ym)
            repository.getExpenseByCategoryInRange(start, end)
        }

        filteredTransactions = _selectedYearMonth.switchMap { ym ->
            val (start, end) = monthRangeMillis(ym)
            repository.getTransactionsInRange(start, end)
        }

        totalExpenseForMonth = selectedYearMonth.switchMap { ym ->
            val (start, end) = monthRangeMillis(ym)
            repository.getTotalExpenseInRange(start, end)
        }

        totalIncomeForMonth = selectedYearMonth.switchMap { ym ->
            val (start, end) = monthRangeMillis(ym)
            repository.getTotalIncomeInRange(start, end)
        }

        sortedTransactions.addSource(filteredTransactions) { applySort() }
        sortedTransactions.addSource(sortNewestFirst) { applySort() }

    }

    fun setSelectedMonth(ym: String) {
        _selectedYearMonth.value = ym
    }

    fun setSortNewestFirst(isNewestFirst: Boolean){
        _sortNewestFirst.value = isNewestFirst
    }

    fun toggleSort() {
        _sortNewestFirst.value = !(_sortNewestFirst.value ?: true)
    }


    fun insert(transaction: Transaction) = viewModelScope.launch {
        repository.insert(transaction)
    }

    fun delete(transaction: Transaction) = viewModelScope.launch {
        repository.delete(transaction)
    }

    private fun applySort() {
        val list = filteredTransactions.value ?: emptyList()
        val newestFirst = sortNewestFirst.value ?: true

        val sorted = if (newestFirst) {
            list.sortedByDescending { it.date }
        } else {
            list.sortedBy { it.date }
        }

        sortedTransactions.value = sorted
    }


    private fun monthRangeMillis(yearMonth: String): Pair<Long, Long> {
        val (yStr, mStr) = yearMonth.split("-")
        val year = yStr.toInt()
        val monthIndex = mStr.toInt() - 1   // Calendar uses 0-based month

        val cal = Calendar.getInstance()
        cal.set(year, monthIndex, 1, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis

        cal.add(Calendar.MONTH, 1)
        cal.add(Calendar.MILLISECOND, -1)
        val end = cal.timeInMillis

        return start to end
    }

    fun currentYearMonth(): String {
        val cal = Calendar.getInstance()
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH) + 1
        return String.format("%04d-%02d", y, m)
    }

}