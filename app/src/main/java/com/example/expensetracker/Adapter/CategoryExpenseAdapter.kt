package com.example.expensetracker.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.data_class.CategoryExpense
import com.example.expensetracker.CategoryUiConfig
import com.example.expensetracker.databinding.ItemCategoryExpenseBinding
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
        val formatted = "₹" + String.format(Locale.getDefault(), "%,.2f", item.total)
        holder.binding.tvCategoryTotal.text = formatted

        val colorRes = CategoryUiConfig.colorRes(item.category)
        val color = ContextCompat.getColor(holder.itemView.context, colorRes)

        holder.binding.cardCategory.setCardBackgroundColor(color)
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<CategoryExpense>) {
        items = newItems
        notifyDataSetChanged()
    }


}