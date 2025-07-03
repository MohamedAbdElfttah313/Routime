package com.example.routime.Presentation.UiAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.routime.Presentation.UiAdapters.StringSelectionRecyclerViewAdapter.PlainStringViewHolder
import com.example.routime.R
import com.example.routime.databinding.PlainStringLayoutBinding

class StringSelectionRecyclerViewAdapter(val stringList: List<String>,val getSelectedStringData : (String)->(Unit))
    : RecyclerView.Adapter<PlainStringViewHolder>() {
    var selectedOptionCheckerImageView : ImageView? = null
    /**
     * This could use some abstraction given you used the class
     * with multiple different attributes in the same xml
     * */
    class PlainStringViewHolder(val xmlView : PlainStringLayoutBinding) : ViewHolder(xmlView.root){
        val plainStringTextView = xmlView.PlainStringTextView
        val checkerImageView = xmlView.PlainStringCheckerImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlainStringViewHolder {
        val binding = PlainStringLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PlainStringViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlainStringViewHolder, position: Int) {
        holder.plainStringTextView.text = stringList[position]
        holder.plainStringTextView.setOnClickListener {
            updateChecker(holder)
            getSelectedStringData(stringList[position])
        }
    }

    override fun getItemCount() = stringList.size

    private fun updateChecker(holder : PlainStringViewHolder){
        selectedOptionCheckerImageView?.let {
            selectedOptionCheckerImageView!!.setImageResource(R.drawable.outline_circle_24)
        }
        holder.checkerImageView.setImageResource(R.drawable.outline_check_circle_24)
        selectedOptionCheckerImageView = holder.checkerImageView
    }
}