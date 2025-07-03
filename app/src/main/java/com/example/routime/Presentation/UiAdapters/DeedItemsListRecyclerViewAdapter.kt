package com.example.routime.Presentation.UiControllers.UiAdapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.routime.Data.Models.Deed
import com.example.routime.databinding.DeedItemLayoutBinding

class DeedItemsListRecyclerViewAdapter(private val deedList : List<Deed>,val showDeed : (Deed)->Unit )
    : RecyclerView.Adapter<DeedItemsListRecyclerViewAdapter.DeedItemViewHolder>() {

    class DeedItemViewHolder(private val xmlView : DeedItemLayoutBinding) : ViewHolder(xmlView.root){
        val deedItemIconImageView = xmlView.DeedItemIconImageView
        val deedItemTitleTextView = xmlView.DeedItemTitleTextView
        val deedItemStartTimeTextView = xmlView.DeedItemStartTimeTextView

        fun bind(deed : Deed,showDeed: (Deed) -> Unit){
            xmlView.root.setOnClickListener {
                showDeed(deed)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeedItemViewHolder {
        val binding = DeedItemLayoutBinding
            .inflate(LayoutInflater.from(parent.context),parent,false)
        return DeedItemViewHolder(binding)
      }

    override fun onBindViewHolder(holder: DeedItemViewHolder, position: Int) {
        holder.deedItemIconImageView.apply {
            setImageResource(deedList[position].iconId)
            imageTintList = ColorStateList.valueOf("#FFFFFF".toColorInt())
        }
        holder.deedItemTitleTextView.text = deedList[position].title
        holder.deedItemStartTimeTextView.text = deedList[position].startTime
            .substringAfter(' ')
            .dropLast(3)

        holder.bind(deedList[position],showDeed)
    }

    override fun getItemCount() = deedList.size
}