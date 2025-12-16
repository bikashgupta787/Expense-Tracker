package com.example.expensetracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensetracker.databinding.FragmentStatsBinding
import java.util.Calendar
import java.util.Locale


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

        // Observe grouped totals — only categories with rows will be returned by DAO
        viewModel.expenseByCategory.observe(viewLifecycleOwner) { list ->
            // If you want to show them sorted (e.g., descending by total), sort here:
            val sorted = list.sortedByDescending { it.total }
            adapter.submitList(sorted)
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

        binding.tvMonthSelector.setOnClickListener {
            val currentYM = viewModel.selectedYearMonth.value ?: viewModel.currentYearMonth()

            MonthYearPickerDialog(currentYM) { selectedYM ->
                viewModel.setSelectedMonth(selectedYM)

                // Formatting for UI: "2025-12" -> "Dec, 2025"
                val parts = selectedYM.split("-")
                val year = parts[0]
                val month = parts[1].toInt()

                val monthName = Calendar.getInstance().apply {
                    set(Calendar.MONTH, month - 1)
                }.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())

                binding.tvMonthSelector.text = "$monthName, $year ▼"
            }.show(childFragmentManager, "monthPicker")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}