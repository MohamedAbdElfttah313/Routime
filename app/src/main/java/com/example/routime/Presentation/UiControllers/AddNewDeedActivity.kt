package com.example.routime.Presentation.UiControllers

import ContextViewModelFactory
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routime.CategoryEnum
import com.example.routime.Data.Models.Deed
import com.example.routime.Emojis
import com.example.routime.ExtrasNames
import com.example.routime.FileType
import com.example.routime.Helpers.FileProviderHelper

import com.example.routime.Presentation.UiAdapters.StringSelectionRecyclerViewAdapter
import com.example.routime.Presentation.UiDialogs.SelectAttachmentBottomSheet
import com.example.routime.Presentation.ViewModels.AddNewDeedViewModel
import com.example.routime.R
import com.example.routime.Utils
import com.example.routime.databinding.ActivityAddNewDeedBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date

class AddNewDeedActivity : AppCompatActivity() {

    private lateinit var xmlView : ActivityAddNewDeedBinding
    private lateinit var activityResultLauncher : ActivityResultLauncher<String>
    private lateinit var fileProviderHelper: FileProviderHelper
    private var selectedFileType = FileType.Undefined
    private var selectedFileUri : Uri? = null
    private var fileDisplayName = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        xmlView = ActivityAddNewDeedBinding.inflate(layoutInflater)
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

        fileProviderHelper = FileProviderHelper(applicationContext)

        var deedFromAnotherScreen : Deed? =null
        if (intent.hasExtra(ExtrasNames.PARCELABLE_DEED.name)){
            deedFromAnotherScreen = intent.getParcelableExtra<Deed>(ExtrasNames.PARCELABLE_DEED.name)
            deedFromAnotherScreen?.let(::populateDeedData)
        }

        xmlView.BackButtonImageView.setOnClickListener { finish() }
        setUpSelectionMenus()

        xmlView.SliderProgressView.addOnChangeListener { _, value, _ ->
            xmlView.SliderProgressTextView.text = "progress : ${value.toInt()}%"
        }

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){uri->
            if (uri==null) return@registerForActivityResult
            
            selectedFileUri = uri
            fileDisplayName = fileProviderHelper.getFileName(uri)?:""
            xmlView.SelectedAttachmentLinearLayout.visibility = View.VISIBLE
            xmlView.SelectedAttachmentTextView.text = fileDisplayName

            val fileTypeIcon = when(selectedFileType){
                FileType.Picture -> R.drawable.ic_image
                FileType.Video -> R.drawable.ic_video
                FileType.Document -> R.drawable.ic_document
                FileType.Audio -> R.drawable.ic_volume
                FileType.Undefined -> null
            }

            fileTypeIcon?.let {
                xmlView.SelectedAttachmentTextView.setCompoundDrawablesWithIntrinsicBounds(fileTypeIcon,0,0,0)
            }

        }

        xmlView.SelectedAttachmentRemove.setOnClickListener {
            selectedFileUri = null
            selectedFileType = FileType.Undefined
            fileDisplayName = ""
            xmlView.SelectedAttachmentLinearLayout.visibility = View.GONE
        }

        xmlView.AddDeedSelectAttachmentButton.setOnClickListener {
            val attachmentSelectionBottomSheet = SelectAttachmentBottomSheet().apply {
                setOnSelection { fileType ->
                    selectedFileType = fileType
                    val mimeType : String?= when(fileType){
                        FileType.Picture -> "image/*"
                        FileType.Video -> "video/*"
                        FileType.Document -> "application/pdf"
                        FileType.Audio -> "audio/*"
                        FileType.Undefined -> null
                    }
                    dismiss()
                    mimeType?.let(activityResultLauncher::launch)
                }
            }

            attachmentSelectionBottomSheet.show(supportFragmentManager,"Attachment_Selection")
        }

        xmlView.SelectedAttachmentTextView.setOnClickListener {
            selectedFileUri?.let {
                val viewIntent = Intent(Intent.ACTION_VIEW,it).apply {
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                startActivity(Intent.createChooser(viewIntent,"View Content"))
            }
        }

        val addNewDeedViewModel : AddNewDeedViewModel by viewModels {
            ContextViewModelFactory(applicationContext)
        }

        xmlView.SaveNewDeedButton.setOnClickListener {
            if (validateFieldsNotEmpty()){
                xmlView.SaveNewDeedButton.apply {
                    isEnabled = false
                    text = "Saving.."
                }

                val category = xmlView.AddDeedSelectedCategoryTextView.text.toString().substringAfterLast(' ')
                val categoryIcon = CategoryEnum.valueOf(category).resId
                val uriToSave = selectedFileUri.run {
                    if (deedFromAnotherScreen!=null){
                        if (deedFromAnotherScreen.attachmentDisplayName == fileDisplayName) return@run selectedFileUri
                        deleteOldFileIfExist(deedFromAnotherScreen.attachmentDisplayName,FileType.valueOf(deedFromAnotherScreen.attachmentType))
                    }

                    if (this!=null) saveDocumentToStorage(this,selectedFileType) else null
                }

                val deed = Deed(xmlView.AddDeedTitleEditText.text.toString(),
                    xmlView.AddDeedDescriptionEditText.text.toString(),
                    deedFromAnotherScreen?.startTime ?: Utils.formatTimeForDataBase(Date()),
                    xmlView.AddDeedTimeSpentEditText.text.toString().toInt(),
                    category,
                    xmlView.SliderProgressView.value.toInt(),
                    xmlView.SliderProgressView.value == 100f,
                    uriToSave?.toString()?:"",selectedFileType.name,
                    fileDisplayName,
                    categoryIcon,
                    xmlView.AddDeedSelectedEmojiTextView.text.toString(),
                    id = deedFromAnotherScreen?.id)

                lifecycleScope.launch{
                    withContext(Dispatchers.IO){
                        if (deedFromAnotherScreen==null){
                            addNewDeedViewModel.insertNewDeed(deed)
                        }else{
                            addNewDeedViewModel.updateDeed(deed)
                        }
                        delay(500)
                    }
                    withContext(Dispatchers.Main){
                        xmlView.SaveNewDeedButton.text = "Saved Successfully"
                        delay(500)
                        this@AddNewDeedActivity.finish()
                    }
                }
            }
        }

    }

    private fun populateDeedData(deed : Deed){
        with(xmlView){
            AddDeedTitleEditText.setText(deed.title)
            AddDeedDescriptionEditText.setText(deed.description)
            AddDeedSelectedCategoryTextView.text = "Category Set To ${deed.category}"
            AddDeedSelectedIconImageView.setImageResource(CategoryEnum.valueOf(deed.category).resId)
            SliderProgressView.value = deed.progress.toFloat()
            SliderProgressTextView.text = "Progress : ${deed.progress}"
            AddDeedSelectedEmojiTextView.text = deed.emojiId
            AddDeedTimeSpentEditText.setText(deed.timeSpent.toString())
            if (deed.attachmentUri.isNotBlank()){
                SelectedAttachmentLinearLayout.visibility = View.VISIBLE
                selectedFileType = FileType.valueOf(deed.attachmentType)
                fileDisplayName = deed.attachmentDisplayName
                selectedFileUri = deed.attachmentUri.toUri()

            }
        }
    }

    private fun setUpSelectionMenus() {
        arrayOf(xmlView.SelectCategoryRecyclerView,xmlView.SelectMoodRecyclerView).forEach {
            it.layoutManager = LinearLayoutManager(this)
            it.itemAnimator = DefaultItemAnimator()
        }

        xmlView.SelectCategoryRecyclerView.adapter = StringSelectionRecyclerViewAdapter(CategoryEnum.entries.map { it.name },::setCategoryName)
        xmlView.SelectMoodRecyclerView.adapter = StringSelectionRecyclerViewAdapter(Emojis.entries.map { it.code },::setEmoji)

        with(xmlView){
            IconAndCategoryLinearLayout.setOnClickListener {
                toggleVisibility(SelectCategoryMainView)
            }

            SelectMoodLinearLayout.setOnClickListener {
                toggleVisibility(SelectMoodMainView)
            }

            SelectCategoryTextView.setOnClickListener { toggleVisibility(SelectCategoryMainView) }
            SelectMoodTextView.setOnClickListener { toggleVisibility(SelectMoodMainView) }
        }
    }

    private fun toggleVisibility(view : View){
        view.visibility = if (view.isVisible) View.GONE else View.VISIBLE
    }

    private fun setCategoryName(category : String) {
        xmlView.AddDeedSelectedCategoryTextView.text = "Category Set To $category"
        xmlView.AddDeedSelectedIconImageView.setImageResource(CategoryEnum.valueOf(category).resId)
        toggleVisibility(xmlView.SelectCategoryMainView)
    }

    private fun setEmoji(emoji : String){
        xmlView.AddDeedSelectedEmojiTextView.text = emoji
        toggleVisibility(xmlView.SelectMoodMainView)

    }


    private fun validateFieldsNotEmpty() : Boolean {
         val allFilled = with(xmlView){
             var filled = true
             if (AddDeedTitleEditText.text.isNullOrBlank()) filled = shakeView(AddDeedTitleEditText)
             if (AddDeedDescriptionEditText.text.isNullOrBlank()) filled =  shakeView(AddDeedDescriptionEditText)
             if (AddDeedSelectedCategoryTextView.text.isNullOrBlank()) filled =  shakeView(IconAndCategoryLinearLayout)
             if (AddDeedTimeSpentEditText.text.isNullOrBlank()) filled = shakeView(TimeSpentLinearLayout)
             filled
        }

        return allFilled
    }

    private fun deleteOldFileIfExist(fileName : String,fileType: FileType){
        if (fileName.isNotBlank()){
            fileProviderHelper.deleteFile(fileName,fileType)
        }
    }

    private fun saveDocumentToStorage(uri : Uri,fileType : FileType):Uri?{
        return when(fileType){
            FileType.Picture -> fileProviderHelper.savePictureToExternalAppStorage(uri)
            FileType.Video -> fileProviderHelper.saveVideosToExternalAppStorage(uri)
            FileType.Document -> fileProviderHelper.saveDocumentToExternalAppStorage(uri)
            FileType.Audio -> fileProviderHelper.saveAudioToExternalAppStorage(uri)
            FileType.Undefined -> null
        }
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