package com.example.expensetracker

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.expensetracker.databinding.DialogEditBudgetBinding

class EditBudgetDialogFragment : DialogFragment() {

    private var _binding: DialogEditBudgetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BudgetViewModel by activityViewModels()

    companion object {
        private const val ARG_CATEGORY = "arg_category"
        private const val ARG_AMOUNT = "arg_amount"

        fun newInstance(category: String, amount: Double): EditBudgetDialogFragment {
            val f = EditBudgetDialogFragment()
            val b = Bundle()
            b.putString(ARG_CATEGORY, category)
            b.putDouble(ARG_AMOUNT, amount)
            f.arguments = b
            return f
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogEditBudgetBinding.inflate(LayoutInflater.from(requireContext()))

        val category = arguments?.getString(ARG_CATEGORY) ?: ""
        val amount = arguments?.getDouble(ARG_AMOUNT) ?: 0.0

        binding.tvDialogTitle.text = "Set budget for $category"
        binding.etBudgetAmount.setText(if (amount > 0.0) amount.toInt().toString() else "")

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setPositiveButton("Save") { _, _ ->
                val text = binding.etBudgetAmount.text.toString()
                val value = text.toDoubleOrNull() ?: 0.0
                //val yearMonth = viewModel.selectedYearMonth.value ?: viewModel.currentYearMonth()
                viewModel.setBudget(category, value)
            }
            .setNegativeButton("Cancel", null)
            .create()

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}