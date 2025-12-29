package com.example.expensetracker.Activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.expensetracker.R
import com.example.expensetracker.data_class.Transaction
import com.example.expensetracker.ViewModel.TransactionViewModel
import com.example.expensetracker.databinding.ActivityAddTransactionBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding
    private var selectedDateInMillis : Long = Date().time
    private lateinit var selectedType:String
    lateinit var expenseAdapter: ArrayAdapter<String>
    lateinit var incomeAdapter: ArrayAdapter<String>

    private val viewModel: TransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)


        expenseAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.expense_categories)
        )

        incomeAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.income_categories)
        )


        binding.actCategory.setAdapter(expenseAdapter)
        //binding.actCategory.setText("") // reset field



//        val categories = resources.getStringArray(R.array.expense_categories)
//        val categoryAdapter = ArrayAdapter(
//            this,
//            android.R.layout.simple_list_item_1,
//            categories
//        )
//        binding.actCategory.setAdapter(categoryAdapter)

        updateSelectedDateText()

        binding.cardDateContainer.setOnClickListener {
            showDateTimePicker()
        }

        binding.imageView.setOnClickListener {
            finish()
        }


        binding.groupType.addOnButtonCheckedListener { _, checkedId, isChecked ->

            if (!isChecked) return@addOnButtonCheckedListener

            when (checkedId) {

                R.id.btnExpense -> {
                    binding.btnExpense.setBackgroundTintList(
                        ColorStateList.valueOf(getColor(R.color.expense_red_active))
                    )
                    binding.btnExpense.setTextColor(getColor(R.color.expense_text_active))

                    binding.btnIncome.setBackgroundTintList(
                        ColorStateList.valueOf(getColor(R.color.income_green_inactive))
                    )
                    binding.btnIncome.setTextColor(getColor(R.color.income_text_inactive))

                    binding.actCategory.setAdapter(expenseAdapter)
                    binding.actCategory.setText("")
                    selectedType = "Expense"
                }

                R.id.btnIncome -> {
                    binding.btnIncome.setBackgroundTintList(
                        ColorStateList.valueOf(getColor(R.color.income_green_active))
                    )
                    binding.btnIncome.setTextColor(getColor(R.color.income_text_active))

                    binding.btnExpense.setBackgroundTintList(
                        ColorStateList.valueOf(getColor(R.color.expense_red_inactive))
                    )
                    binding.btnExpense.setTextColor(getColor(R.color.expense_text_inactive))

                    binding.actCategory.setAdapter(incomeAdapter)
                    binding.actCategory.setText("")
                    selectedType = "Income"
                }
            }
        }

        // SELECT DEFAULT
        binding.groupType.check(R.id.btnExpense)


        binding.btnSave.setOnClickListener {
            val title =  binding.etTitle.text.toString()
            val amount =  binding.etAmount.text.toString().toDoubleOrNull() ?: 0.0
            val selectedTypeId =  selectedType.toString().trim()
            val category = binding.actCategory.text.toString().trim()

            if (title.isEmpty() || selectedTypeId.isEmpty() || category.isEmpty()){
                Toast.makeText(this, "Please enter all the details", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val transaction = Transaction(
                title = title,
                amount = amount,
                type = selectedTypeId,
                date = selectedDateInMillis,
                category = category
            )

            viewModel.insert(transaction)
            finish()
        }
    }

    private fun showDateTimePicker() {
        // First show DatePicker
        val nowCal = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // When date selected, show TimePicker
                val selectedCal = Calendar.getInstance()
                selectedCal.set(Calendar.YEAR, year)
                selectedCal.set(Calendar.MONTH, month)
                selectedCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Show time picker to pick hour/minute
                val timePicker = TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        selectedCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        selectedCal.set(Calendar.MINUTE, minute)
                        selectedCal.set(Calendar.SECOND, 0)
                        selectedCal.set(Calendar.MILLISECOND, 0)

                        selectedDateInMillis = selectedCal.timeInMillis
                        updateSelectedDateText()
                    },
                    nowCal.get(Calendar.HOUR_OF_DAY),
                    nowCal.get(Calendar.MINUTE),
                    false
                )
                timePicker.show()
            },
            nowCal.get(Calendar.YEAR),
            nowCal.get(Calendar.MONTH),
            nowCal.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }

    // show only date in tvSelectedDate (the RecyclerView will also show only date)
    private fun updateSelectedDateText() {
        val formattedDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            .format(Date(selectedDateInMillis))
        // show date + time in picker preview so user sees picked time as well
        binding.tvSelectedDate.text = formattedDate
    }

}