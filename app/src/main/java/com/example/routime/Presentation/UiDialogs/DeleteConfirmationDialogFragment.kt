package com.example.routime.Presentation.UiDialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.routime.R
import androidx.core.graphics.drawable.toDrawable
import com.example.routime.databinding.FragmentDeleteConfirmationBinding

class DeleteConfirmationDialogFragment : DialogFragment() {

    private lateinit var xmlView : FragmentDeleteConfirmationBinding
    private var  onCancelPressed : (()->(Unit))?  = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        xmlView = FragmentDeleteConfirmationBinding.inflate(inflater,container,false)
        if (dialog!=null && dialog!!.window!=null){
            dialog!!.window!!.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }
        return xmlView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        xmlView.btnDelete.setOnClickListener {
            onCancelPressed?.invoke()
        }

        xmlView.btnCancel.setOnClickListener {
            dismiss()
        }

    }

    fun setOnCancelPressed(onCancelled : ()->(Unit)){
        onCancelPressed = onCancelled
    }

}