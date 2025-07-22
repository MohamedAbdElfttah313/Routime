package com.example.routime.Presentation.UiDialogs

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routime.CategoryEnum
import com.example.routime.Data.Models.Category
import com.example.routime.Presentation.UiAdapters.ImageViewSelectionRecyclerViewAdapter
import com.example.routime.R
import com.example.routime.databinding.FragmentAddCategoryBinding

class AddCategoryDialogFragment : DialogFragment() {
    private lateinit var xmlView : FragmentAddCategoryBinding
    private var onAddCategoryPressed : ((Category)->(Boolean))? = null
    private var selectedIconId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        xmlView = FragmentAddCategoryBinding.inflate(inflater,container,false)
        if (dialog!=null && dialog!!.window!=null){
            dialog!!.window!!.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }
        selectedIconId = R.drawable.ic_language
        return xmlView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        xmlView.CancelCategoryInsertionButton.setOnClickListener {
            dismiss()
        }

        xmlView.SaveNewCategoryButton.setOnClickListener {
            if (validateFieldsNotEmpty()){
                xmlView.CancelCategoryInsertionButton.isEnabled = false
                val category = Category(xmlView.AddNewCategoryNameEditText.text.toString(),
                    xmlView.AddNewCategoryTypeEditText.text.toString(),
                    selectedIconId)
                val insertionSuccessful = onAddCategoryPressed?.invoke(category)?:false
                if (insertionSuccessful){
                    dismiss()
                }
            }
        }

        with(xmlView.CategoryIconsRecyclerView){
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            itemAnimator = DefaultItemAnimator()
            adapter = ImageViewSelectionRecyclerViewAdapter(CategoryEnum.entries.map { it.resId },::setNewCategorySelectedIcon)
        }
    }

    fun setOnAddNewCategoryListener(onAddPressed : (Category)->(Boolean)){
        onAddCategoryPressed = onAddPressed
    }

    private fun setNewCategorySelectedIcon(resId : Int){
        xmlView.AddNewCategorySelectedIconImageView.setImageResource(resId)
        selectedIconId = resId
    }

    private fun validateFieldsNotEmpty():Boolean{
        var filled = true
        with(xmlView){
            if (AddNewCategoryNameEditText.text.isNullOrBlank()) filled = shakeView(AddNewCategoryNameEditText)
            if (AddNewCategoryTypeEditText.text.isNullOrBlank()) filled = shakeView(AddNewCategoryTypeEditText)
        }
        return filled
    }

    private fun shakeView(view: View, duration: Long = 1000, distance: Float = 10f) : Boolean{
        val animator = ObjectAnimator.ofFloat(
            view,
            View.TRANSLATION_X, // Animate the X translation
            0f, // Start at original position
            distance, // Move right
            -distance, // Move left
            distance, // Move right
            -distance, // Move left
            0f // Return to original position
        ).apply {
            setDuration(duration)
            doOnStart {
                view.backgroundTintList = ColorStateList.valueOf("#5DF06565".toColorInt())
            }
            doOnEnd {
                view.backgroundTintList = ColorStateList.valueOf("#26000000".toColorInt())
            }
        }
        animator.start()

        return false
    }
}