package com.example.expensetracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensetracker.Adapter.BudgetAdapter
import com.example.expensetracker.databinding.FragmentBudgetBinding
import java.util.Calendar
import java.util.Locale

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BudgetViewModel by activityViewModels()

    private lateinit var adapter: BudgetAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = BudgetAdapter(onEditClicked = { budgetUi ->
            // open dialog
            val dialog = EditBudgetDialogFragment.newInstance(budgetUi.category, budgetUi.budgetAmount ?: 0.0)
            dialog.show(childFragmentManager, "editBudget")
        })

        binding.rvBudgets.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBudgets.adapter = adapter

        viewModel.budgetUiList.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)

            viewModel.availableForMonth.observe(viewLifecycleOwner) { available ->
                binding.tvAvailableAmount.text = formatCurrency(available)
            }

            viewModel.totalBudgetForMonth.observe(viewLifecycleOwner) { totalBudget ->
                val tb = totalBudget ?: 0.0
                binding.tvProgressRight.text = formatCurrency(tb)
                updateProgressBar(tb, viewModel.totalSpentForMonth.value ?: 0.0)
            }

            viewModel.totalSpentForMonth.observe(viewLifecycleOwner) { spent ->
                val s = spent ?: 0.0
                binding.tvProgressLeft.text = formatCurrency(s)
                updateProgressBar(viewModel.totalBudgetForMonth.value ?: 0.0, s)
            }

            viewModel.percentUsedForMonth.observe(viewLifecycleOwner) { percent ->
                binding.totalProgress.progress = percent
                // set color based on percent thresholds
                val colorRes = when {
                    percent <= 70 -> R.color.budget_green
                    percent <= 90 -> R.color.budget_amber
                    else -> R.color.budget_red
                }
                binding.totalProgress.setIndicatorColor(requireContext().getColor(colorRes))

                binding.tvMonthSelector.setOnClickListener {
                    val currentYm = viewModel.selectedYearMonth.value!!
                    MonthYearPickerDialog(currentYm) { selectedYm ->
                        viewModel.setSelectedYearMonth(selectedYm)

                        // format "2025-12" → "Dec, 2025"
                        val parts = selectedYm.split("-")
                        val year = parts[0]
                        val month = parts[1].toInt()
                        val monthName = Calendar.getInstance().apply { set(Calendar.MONTH, month - 1) }
                            .getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())

                        binding.tvMonthSelector.text = "$monthName, $year ▼"
                    }.show(childFragmentManager, "monthPicker")
                }


            }

        }
    }

    private fun updateProgressBar(totalBudget: Double, spent: Double) {
        val percent = if (totalBudget > 0.0) ((spent / totalBudget) * 100.0).coerceIn(0.0, 100.0) else 0.0
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