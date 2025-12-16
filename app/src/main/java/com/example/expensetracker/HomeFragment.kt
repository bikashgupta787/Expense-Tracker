package com.example.expensetracker

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar
import java.util.Locale

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
        }


        viewModel.totalExpenseForMonth.observe(viewLifecycleOwner) { expense ->
            val value = expense ?: 0.0
            binding.tvExpense.text = "₹${String.format("%,.0f", value)}"
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

//        // Swipe-to-delete
//        val itemTouch = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
//            override fun onMove(
//                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
//            ): Boolean = false
//
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                val pos = viewHolder.adapterPosition
//                val transaction = transactionList[pos]
//                viewModel.delete(transaction)
//                transactionList.removeAt(pos)
//                adapter.notifyItemRemoved(pos)
//            }
//        }
//        ItemTouchHelper(itemTouch).attachToRecyclerView(binding.rvTransactions)

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
                // DO NOT modify adapter list manually
                // submitList() will auto-update when DB LiveData emits new list
            }
        }

        ItemTouchHelper(itemTouch).attachToRecyclerView(binding.rvTransactions)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}