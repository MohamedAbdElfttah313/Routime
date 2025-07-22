package com.example.routime.Presentation.UiAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.transition.Visibility
import com.example.routime.Data.Models.Category
import com.example.routime.databinding.ItemCategoryLayoutBinding

class CategoryItemsRecyclerViewAdapter(val categories : List<Category>)
    : RecyclerView.Adapter<CategoryItemsRecyclerViewAdapter.CategoryItemViewHolder>() {

    class CategoryItemViewHolder(val xmlView : ItemCategoryLayoutBinding) : ViewHolder(xmlView.root){
        val singleCategoryIconImageView = xmlView.SingleCategoryIconImageView
        val singleCategoryNameTextView = xmlView.SingleCategoryNameTextView
        val singleCategoryTypeTextView = xmlView.SingleCategoryTypeTextView
        val singleCategoryEditButton = xmlView.SingleCategoryEditImageButton
        val singleCategoryDeleteButton = xmlView.SingleCategoryDeleteImageButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryItemViewHolder {
        val binding = ItemCategoryLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CategoryItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryItemViewHolder, position: Int) {
        holder.singleCategoryIconImageView.setImageResource(categories[position].iconId)
        holder.singleCategoryNameTextView.text = categories[position].name
        holder.singleCategoryTypeTextView.text = categories[position].type

        val buttonVisibility = if (categories[position].type == "Default") View.GONE else View.VISIBLE
        holder.singleCategoryEditButton.visibility = buttonVisibility
        holder.singleCategoryDeleteButton.visibility = buttonVisibility


    }

    override fun getItemCount() = categories.size
}