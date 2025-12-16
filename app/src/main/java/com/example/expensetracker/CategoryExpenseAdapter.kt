package com.example.expensetracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.databinding.ItemCategoryExpenseBinding
import java.text.NumberFormat
import java.util.Locale

class CategoryExpenseAdapter(
    private var items: List<CategoryExpense> = emptyList()
) : RecyclerView.Adapter<CategoryExpenseAdapter.VH>() {

    class VH(val binding: ItemCategoryExpenseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCategoryExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.binding.tvCategoryName.text = item.category

        // Format currency based on locale (you can force INR if you want)
        val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
        // If you want INR symbol and Locale doesn't show it, you can do a manual format:
        // val formatted = "₹${String.format(Locale.getDefault(), \"%,.2f\", item.total)}"
        val formatted = "₹" + String.format(Locale.getDefault(), "%,.2f", item.total)
        holder.binding.tvCategoryTotal.text = formatted
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<CategoryExpense>) {
        items = newItems
        notifyDataSetChanged()
    }
}