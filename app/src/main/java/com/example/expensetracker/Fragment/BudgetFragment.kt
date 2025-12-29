package com.example.expensetracker.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensetracker.Adapter.BudgetAdapter
import com.example.expensetracker.ViewModel.BudgetViewModel
import com.example.expensetracker.MonthYearPickerDialog
import com.example.expensetracker.R
import com.example.expensetracker.databinding.FragmentBudgetBinding
import java.util.Calendar
import java.util.Locale

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BudgetViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
    }

    private lateinit var adapter: BudgetAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = BudgetAdapter { budgetUi ->
            EditBudgetDialogFragment.newInstance(budgetUi.category, budgetUi.budgetAmount ?: 0.0)
                .show(childFragmentManager, "editBudget")
        }

        binding.rvBudgets.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBudgets.adapter = adapter

        viewModel.budgetUiList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.progressData.observe(viewLifecycleOwner) { (budget, spent) ->
            binding.tvProgressRight.text = formatCurrency(budget)
            binding.tvProgressLeft.text = formatCurrency(spent)
        //updateProgressBar(budget, spent)
        }


        viewModel.availableForMonth.observe(viewLifecycleOwner) {
            binding.tvAvailableAmount.text = formatCurrency(it)
        }

        viewModel.percentUsedForMonth.observe(viewLifecycleOwner) { percent ->
            binding.totalProgress.progress = percent
            val colorRes = when {
                percent <= 70 -> R.color.budget_green
                percent <= 90 -> R.color.budget_amber
                else -> R.color.budget_red
            }
            binding.totalProgress.setIndicatorColor(requireContext().getColor(colorRes))
        }

        viewModel.selectedYearMonth.observe(viewLifecycleOwner) { ym ->
            val parts = ym.split("-")
            val year = parts[0]
            val month = parts[1].toInt()
            val monthName = Calendar.getInstance().apply {
                set(Calendar.MONTH, month - 1)
            }.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
            binding.tvMonthSelector.text = "$monthName, $year"
        }

        binding.rowMonthSelector.setOnClickListener {
            val currentYm = viewModel.selectedYearMonth.value ?: return@setOnClickListener
            MonthYearPickerDialog(currentYm) {
                viewModel.setSelectedYearMonth(it)
            }.show(childFragmentManager, "monthPicker")
        }
    }

    private fun updateProgressBar(totalBudget: Double, spent: Double) {
        val percent =
            if (totalBudget > 0.0) ((spent / totalBudget) * 100.0).coerceIn(0.0, 100.0) else 0.0
        binding.totalProgress.progress = percent.toInt()
    }

    private fun formatCurrency(value: Double): String {
        // simple INR formatting, change as needed
        return "₹" + String.format(Locale.getDefault(), "%,.0f", value)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}