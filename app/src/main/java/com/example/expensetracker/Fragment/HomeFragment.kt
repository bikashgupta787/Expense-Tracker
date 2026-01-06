package com.example.expensetracker.Fragment

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.Adapter.TransactionAdapter
import com.example.expensetracker.Activity.AddTransactionActivity
import com.example.expensetracker.MonthYearPickerDialog
import com.example.expensetracker.R
import com.example.expensetracker.data_class.Transaction
import com.example.expensetracker.ViewModel.TransactionViewModel
import com.example.expensetracker.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import java.text.DateFormatSymbols

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransactionViewModel by activityViewModels()

    private val transactionList = mutableListOf<Transaction>()
    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Setup RecyclerView & adapter
        adapter = TransactionAdapter()
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransactions.adapter = adapter

        viewModel.sortedTransactions.observe(viewLifecycleOwner) { list ->

            adapter.submitList(list)

            if (list.isEmpty()) {
                binding.layoutEmptyState.visibility = View.VISIBLE
                binding.rvTransactions.visibility = View.GONE
            } else {
                binding.layoutEmptyState.visibility = View.GONE
                binding.rvTransactions.visibility = View.VISIBLE
            }
        }


//        viewModel.sortedTransactions.observe(viewLifecycleOwner) { list ->
//            adapter.submitList(list)
//        }


        viewModel.totalExpenseForMonth.observe(viewLifecycleOwner) { expense ->
            val value = expense ?: 0.0
            binding.tvExpense.text = "₹${String.format("%,.0f", value)}"
        }

        viewModel.selectedYearMonth.observe(viewLifecycleOwner) { ym ->
            binding.tvMonthSelector.text = "${formatYearMonth(ym)}"
            Log.d("MonthDebug", "selectedYearMonth = $ym")

        }



//        binding.tvMonthSelector.setOnClickListener {
//            val currentYM = viewModel.selectedYearMonth.value ?: viewModel.currentYearMonth()
//
//            MonthYearPickerDialog(currentYM) { selectedYM ->
//                viewModel.setSelectedMonth(selectedYM)
//
//                // Formatting for UI: "2025-12" -> "Dec, 2025"
//                val parts = selectedYM.split("-")
//                val year = parts[0]
//                val month = parts[1].toInt()
//
//                val monthName = Calendar.getInstance().apply {
//                    set(Calendar.MONTH, month - 1)
//                }.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
//
//                binding.tvMonthSelector.text = "$monthName, $year ▼"
//            }.show(childFragmentManager, "monthPicker")
//        }

        binding.rowMonthSelector.setOnClickListener {
            val currentYM = viewModel.selectedYearMonth.value ?: viewModel.currentYearMonth()

            MonthYearPickerDialog(currentYM) { selectedYM ->
                // ONLY update the ViewModel
                viewModel.setSelectedMonth(selectedYM)
            }.show(childFragmentManager, "monthPicker")
        }


        binding.btnAddFirstExpense.setOnClickListener {
            startActivity(Intent(requireContext(), AddTransactionActivity::class.java))
        }


        // FAB opens AddTransactionActivity
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(requireContext(), AddTransactionActivity::class.java))
        }

        // Sort toggle
        binding.btnSort.setOnClickListener {
            viewModel.toggleSort()
            val newest = viewModel.sortNewestFirst.value ?: true
            Toast.makeText(requireContext(), if (newest) "Showing newest first" else "Showing oldest first", Toast.LENGTH_SHORT).show()
        }


        val itemTouch = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.bindingAdapterPosition
                val transaction = adapter.currentList[pos]

                // delete from DB via ViewModel
                viewModel.delete(transaction)

                Snackbar.make(binding.root, "Transaction deleted", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        viewModel.insert(transaction)
                    }.show()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)
                val background = ColorDrawable(Color.parseColor("#D93636")) // red

                if (dX > 0) { // Swipe right

                    background.setBounds(
                        itemView.left,
                        itemView.top,
                        itemView.left + dX.toInt(),
                        itemView.bottom
                    )

                    background.draw(c)

                    icon?.let {
                        val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                        val top = itemView.top + (itemView.height - it.intrinsicHeight) / 2
                        val bottom = top + it.intrinsicHeight
                        val left = itemView.left + iconMargin
                        val right = left + it.intrinsicWidth
                        it.setBounds(left, top, right, bottom)
                        it.draw(c)
                    }

                } else if (dX < 0) { // Swipe left

                    background.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )

                    background.draw(c)

                    icon?.let {
                        val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                        val top = itemView.top + (itemView.height - it.intrinsicHeight) / 2
                        val bottom = top + it.intrinsicHeight
                        val right = itemView.right - iconMargin
                        val left = right - it.intrinsicWidth

                        it.setBounds(left, top, right, bottom)
                        it.draw(c)
                    }
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        ItemTouchHelper(itemTouch).attachToRecyclerView(binding.rvTransactions)

    }

    private fun formatYearMonth(yearMonth: String): String {
        val parts = yearMonth.split("-")
        val year = parts[0]
        val month = parts[1].toInt() - 1  // works for "1" and "01"

        val monthName = DateFormatSymbols.getInstance()
            .shortMonths[month]

        return "$monthName, $year"
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}