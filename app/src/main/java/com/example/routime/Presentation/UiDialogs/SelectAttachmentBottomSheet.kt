package com.example.routime.Presentation.UiDialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.routime.FileType
import com.example.routime.databinding.SelectAttachmentBottomSheetLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SelectAttachmentBottomSheet : BottomSheetDialogFragment() {
    private lateinit var xmlView : SelectAttachmentBottomSheetLayoutBinding
    private var onSelection : ((FileType)->(Unit))? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        xmlView = SelectAttachmentBottomSheetLayoutBinding.inflate(inflater,container,false)
        return xmlView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(xmlView){
            SelectAttachmentPictureTextView.setOnClickListener { onSelection?.invoke(FileType.Picture)?:return@setOnClickListener }
            SelectAttachmentVideoTextView.setOnClickListener { onSelection?.invoke(FileType.Video)?:return@setOnClickListener }
            SelectAttachmentDocumentTextView.setOnClickListener { onSelection?.invoke(FileType.Document)?:return@setOnClickListener }
            SelectAttachmentAudioTextView.setOnClickListener { onSelection?.invoke(FileType.Audio)?:return@setOnClickListener }
        }
    }

    fun setOnSelection(onSelection : (FileType)->(Unit)){
        this.onSelection = onSelection
    }
}