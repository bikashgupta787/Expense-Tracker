package com.example.expensetracker

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.expensetracker.databinding.DialogMonthYearBinding
import java.text.DateFormatSymbols
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

        // Month names
        val months = DateFormatSymbols.getInstance().shortMonths
            .take(12) // removes empty last item

        val parts = initialYearMonth.split("-")
        var year = parts[0].toInt()
        var month = parts[1].toInt() - 1

        binding.npMonth.minValue = 0
        binding.npMonth.maxValue = months.size - 1
        binding.npMonth.displayedValues = months.toTypedArray()
        binding.npMonth.value = month
        binding.npMonth.wrapSelectorWheel = true

        binding.npYear.minValue = minYear
        binding.npYear.maxValue = maxYear
        binding.npYear.value = year
        binding.npYear.wrapSelectorWheel = false

//        return AlertDialog.Builder(requireContext())
//            .setTitle("Select month")
//            .setView(binding.root)
//            .setPositiveButton("OK") { _, _ ->
//                val y = binding.npYear.value
//                val m = binding.npMonth.value
//                val ym = String.format(Locale.getDefault(), "%04d-%02d", y, m)
//                listener(ym)
//            }
//            .setNegativeButton("Cancel", null)
//            .create()

        return AlertDialog.Builder(requireContext())
            .setTitle("Select month")
            .setView(binding.root)
            .setPositiveButton("OK") { _, _ ->
                val selectedMonth =  binding.npMonth.value + 1
                val selectedYear =  binding.npYear.value

                val yearMonth = String.format(
                    Locale.getDefault(),
                    "%04d-%02d",
                    selectedYear,
                    selectedMonth
                )
                listener(yearMonth)
            }
            .setNegativeButton("CANCEL", null)
            .create()
    }
}
