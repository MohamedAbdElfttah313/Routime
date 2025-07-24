package com.example.routime.Presentation.UiControllers

import ContextViewModelFactory
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.routime.CategoryEnum
import com.example.routime.Data.Models.Deed
import com.example.routime.ExtrasNames
import com.example.routime.FileType
import com.example.routime.Helpers.FileProviderHelper
import com.example.routime.Presentation.UiDialogs.DeleteConfirmationDialogFragment
import com.example.routime.Presentation.ViewModels.ShowDeedViewModel
import com.example.routime.R
import com.example.routime.Utils
import com.example.routime.databinding.ActivityShowDeedBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShowDeedActivity : AppCompatActivity() {



    private lateinit var xmlView : ActivityShowDeedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        xmlView = ActivityShowDeedBinding.inflate(layoutInflater)
        setContentView(xmlView.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.R){
            window.insetsController?.let {
                it.hide(WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }else{
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility=(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_IMMERSIVE)
        }

        xmlView.BackButtonImageView.setOnClickListener{
            finish()
        }

        val deed = intent.getParcelableExtra(ExtrasNames.PARCELABLE_DEED.name) as? Deed
        deed?.let { mDeed->
            populateDeedInfo(mDeed)
            if (mDeed.attachmentUri.isNotBlank()){
                xmlView.ShowDeedAttachmentTextView.setOnClickListener {
                    val attachmentIntent = Intent(Intent.ACTION_VIEW,mDeed.attachmentUri.toUri()).apply {
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                    startActivity(Intent.createChooser(attachmentIntent,"Content View"))
                }
            }
        }

        val showDeedViewModel : ShowDeedViewModel by viewModels {
            ContextViewModelFactory(applicationContext)
        }

        xmlView.DeleteDeedButton.setOnClickListener {
            DeleteConfirmationDialogFragment().apply {
                setOnDeletePressed {
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO){
                            deed?.let{
                                showDeedViewModel.deleteDeed(it)
                                if (it.attachmentUri.isNotBlank()){
                                    FileProviderHelper(applicationContext).deleteFile(it.attachmentDisplayName,
                                        FileType.valueOf(it.attachmentType))
                                }
                            }
                        }
                        finish()
                    }
            }
                show(supportFragmentManager,"DeleteConfirmationFragment")
        }
    }

        xmlView.EditDeedButton.setOnClickListener {
            Intent(this,AddNewDeedActivity::class.java).apply {
                if (deed!=null) putExtra(ExtrasNames.PARCELABLE_DEED.name,deed)
            }.also(::startActivity)
            finish()
        }
    }

    private fun populateDeedInfo(deed : Deed){
        with(xmlView){
            ShowDeedTitleTextView.text = deed.title
            ShowDeedIconTextView.setImageResource(CategoryEnum.valueOf(deed.category).resId)
            ShowDeedCategoryPlaceholderTextView.text = deed.category.lowercase() + " Activity"
            ShowDeedDescriptionTextView.text = deed.description
            ShowDeedCategoryTextView.text = deed.category
            ShowDeedMoodTextView.text = deed.emojiId
            ShowDeedTimeSpentTextView.text = "${(deed.timeSpent/60)}h ${deed.timeSpent%60}m"
            ShowDeedStartTimeTextView.text = Utils.formatTimeForDataBase(
                Utils.parseTimeReturnedFromDataBase(deed.startTime),
                "HH:mm MMMM d, yyyy")
            ShowDeedProgressTextView.text = "Progress : ${deed.progress}%"

            if (deed.attachmentUri.isNotBlank()){
                ShowDeedAttachmentTextView.text = deed.attachmentDisplayName
                val fileTypeIcon= when(FileType.valueOf(deed.attachmentType)){
                    FileType.Picture -> R.drawable.ic_image
                    FileType.Video -> R.drawable.ic_video
                    FileType.Document -> R.drawable.ic_document
                    FileType.Audio -> R.drawable.ic_volume
                    FileType.Undefined -> R.drawable.ic_help
                }
                ShowDeedAttachmentTextView.setCompoundDrawablesWithIntrinsicBounds(fileTypeIcon,0,0,0)
            }else{
                ShowDeedAttachmentTextView.text = "No Attachment Found"
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus){
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_IMMERSIVE)
        }
    }
}