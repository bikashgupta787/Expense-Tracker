package com.example.expensetracker.Fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensetracker.Adapter.CategoryExpenseAdapter
import com.example.expensetracker.data_class.CategoryExpense
import com.example.expensetracker.CategoryUiConfig
import com.example.expensetracker.MonthYearPickerDialog
import com.example.expensetracker.ViewModel.TransactionViewModel
import com.example.expensetracker.databinding.FragmentStatsBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import java.text.DateFormatSymbols


class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransactionViewModel by activityViewModels()
    private lateinit var adapter: CategoryExpenseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = CategoryExpenseAdapter()
        binding.rvCategoryExpenses.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategoryExpenses.adapter = adapter

        setupPieChart()

        // Observe grouped totals — only categories with rows will be returned by DAO
        viewModel.expenseByCategory.observe(viewLifecycleOwner) { list ->
            // If you want to show them sorted (e.g., descending by total), sort here:
            val sorted = list.sortedByDescending { it.total }
            adapter.submitList(sorted)
            updatePieChart(sorted)
        }

//        viewModel.totalIncome.observe(viewLifecycleOwner) {
//            binding.tvIncome.text = "₹${it ?: 0.0}"
//        }

//        viewModel.totalExpense.observe(viewLifecycleOwner) {
//            binding.tvExpense.text = "₹${it ?: 0.0}"
//        }

        viewModel.totalExpenseForMonth.observe(viewLifecycleOwner) { expense ->
            val value = expense ?: 0.0
            binding.tvExpense.text = "₹${String.format("%,.0f", value)}"
        }

        viewModel.totalIncomeForMonth.observe(viewLifecycleOwner) { income ->
            val value = income ?: 0.0
            binding.tvIncome.text = "₹${String.format("%,.0f", value)}"
        }

        viewModel.selectedYearMonth.observe(viewLifecycleOwner) { ym ->
            binding.tvMonthSelector.text = "${formatYearMonth(ym)}"
        }

        binding.rowMonthSelector.setOnClickListener {
            val currentYM = viewModel.selectedYearMonth.value ?: viewModel.currentYearMonth()

            MonthYearPickerDialog(currentYM) { selectedYM ->
                // ONLY update the ViewModel
                viewModel.setSelectedMonth(selectedYM)
            }.show(childFragmentManager, "monthPicker")
        }



    }

    private fun setupPieChart() {
        binding.pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 50f
            transparentCircleRadius = 55f
            setDrawEntryLabels(false)
            setUsePercentValues(true)

            legend.apply {
                isEnabled = true
                verticalAlignment = Legend.LegendVerticalAlignment.CENTER
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                orientation = Legend.LegendOrientation.VERTICAL
                setDrawInside(false)
                textColor = Color.WHITE
            }
        }
    }

    private fun formatYearMonth(yearMonth: String): String {
        val parts = yearMonth.split("-")
        val year = parts[0]
        val month = parts[1].toInt() - 1  // works for "1" and "01"

        val monthName = DateFormatSymbols.getInstance()
            .shortMonths[month]

        return "$monthName, $year"
    }

    private fun updatePieChart(data: List<CategoryExpense>) {

        if (data.isEmpty()) {
            binding.pieChart.clear()
            binding.pieChart.invalidate()
            return
        }

        val entries = data.map {
            PieEntry(it.total.toFloat(), it.category)
        }

        val dataSet = PieDataSet(entries, "").apply {
            sliceSpace = 2f
            selectionShift = 5f

            // Category colors (reuse same colors as RecyclerView)
            colors = data.map {
                ContextCompat.getColor(
                    requireContext(),
                    CategoryUiConfig.colorRes(it.category)
                )
            }

            valueTextColor = Color.WHITE
            valueTextSize = 10f
        }

        val pieData = PieData(dataSet).apply {
            setValueFormatter(PercentFormatter(binding.pieChart))
        }

        binding.pieChart.data = pieData
        binding.pieChart.invalidate()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}