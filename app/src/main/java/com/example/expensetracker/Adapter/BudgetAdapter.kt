package com.example.expensetracker.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.BudgetUi
import com.example.expensetracker.databinding.ItemCategoryBudgetBinding
import java.util.Locale

class BudgetAdapter(
    private var items: List<BudgetUi> = emptyList(),
    private val onEditClicked: (BudgetUi) -> Unit
) : RecyclerView.Adapter<BudgetAdapter.VH>() {

    inner class VH(val binding: ItemCategoryBudgetBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCategoryBudgetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvCategoryName.text = item.category
            // description optional - map category to desc if you want
            tvCategoryDesc.text = ""

            tvBudgetAmount.text = if (item.budgetAmount != null && item.budgetAmount > 0.0)
                "₹${String.format(Locale.getDefault(), "%,.0f", item.budgetAmount)}" else "Not Set"

            tvSpentAmount.text = "₹${String.format(Locale.getDefault(), "%,.0f", item.spentAmount)}"
            tvAvailableAmount.text = "₹${String.format(Locale.getDefault(), "%,.0f", item.availableAmount)}"

            btnEditBudget.text = if (item.isSet) "EDIT" else "SET"
            btnEditBudget.setOnClickListener { onEditClicked(item) }
        }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<BudgetUi>) {
        items = newItems
        notifyDataSetChanged()
    }
}