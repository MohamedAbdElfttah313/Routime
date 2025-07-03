package com.example.routime.Presentation.UiAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.routime.Presentation.UiAdapters.ImageViewSelectionRecyclerViewAdapter.PlainImageViewHolder
import com.example.routime.Presentation.UiAdapters.StringSelectionRecyclerViewAdapter.PlainStringViewHolder
import com.example.routime.databinding.PlainImageViewLayoutBinding
import com.example.routime.databinding.PlainStringLayoutBinding

class ImageViewSelectionRecyclerViewAdapter (val imageList: List<Int>,val getSelectedImageData : (Int)->(Unit))
    : RecyclerView.Adapter<PlainImageViewHolder>() {


    class PlainImageViewHolder(val xmlView : PlainImageViewLayoutBinding) : ViewHolder(xmlView.root){
        val plainImageImageView = xmlView.PlainImageImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlainImageViewHolder {
        val binding = PlainImageViewLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PlainImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlainImageViewHolder, position: Int) {
        holder.plainImageImageView.setImageResource(imageList[position])
        holder.plainImageImageView.setOnClickListener {
            getSelectedImageData(imageList[position])
        }
    }

    override fun getItemCount() = imageList.size
}