package com.example.expensetracker

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.expensetracker.databinding.DialogMonthYearBinding
import java.util.Locale

// MonthYearPickerDialog.kt
class MonthYearPickerDialog(
    private val initialYearMonth: String,
    private val minYear: Int = 2000,
    private val maxYear: Int = 2100,
    private val listener: (yearMonth: String) -> Unit
) : DialogFragment() {

    private lateinit var binding: DialogMonthYearBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogMonthYearBinding.inflate(LayoutInflater.from(requireContext()))

        val parts = initialYearMonth.split("-")
        var year = parts[0].toInt()
        var month = parts[1].toInt()

        binding.npMonth.minValue = 1
        binding.npMonth.maxValue = 12
        binding.npMonth.value = month

        binding.npYear.minValue = minYear
        binding.npYear.maxValue = maxYear
        binding.npYear.value = year

        return AlertDialog.Builder(requireContext())
            .setTitle("Select month")
            .setView(binding.root)
            .setPositiveButton("OK") { _, _ ->
                val y = binding.npYear.value
                val m = binding.npMonth.value
                val ym = String.format(Locale.getDefault(), "%04d-%02d", y, m)
                listener(ym)
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}
