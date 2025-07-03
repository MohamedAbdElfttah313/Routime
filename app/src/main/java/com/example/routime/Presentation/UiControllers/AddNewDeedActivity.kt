package com.example.routime.Presentation.UiControllers

import ContextViewModelFactory
import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.graphics.toColorInt
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

import com.example.routime.Presentation.UiAdapters.StringSelectionRecyclerViewAdapter
import com.example.routime.Presentation.ViewModels.AddNewDeedViewModel
import com.example.routime.R
import com.example.routime.Utils
import com.example.routime.databinding.ActivityAddNewDeedBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class AddNewDeedActivity : AppCompatActivity() {

    private lateinit var xmlView : ActivityAddNewDeedBinding
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

        var deedFromAnotherScreen : Deed? =null
        if (intent.hasExtra(ExtrasNames.PARCELABLE_DEED.name)){
            deedFromAnotherScreen = intent.getParcelableExtra<Deed>(ExtrasNames.PARCELABLE_DEED.name)
            deedFromAnotherScreen?.let(::populateDeedData)
        }

        xmlView.BackButtonImageView.setOnClickListener { finish() }
        setUpSelectionMenus()

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
                val deed = Deed(xmlView.AddDeedTitleEditText.text.toString(),
                    xmlView.AddDeedDescriptionEditText.text.toString(),
                    deedFromAnotherScreen?.startTime ?: Utils.formatTimeForDataBase(Date()),
                    xmlView.AddDeedTimeSpentEditText.text.toString().toInt(),
                    category,
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
            AddDeedSelectedEmojiTextView.text = deed.emojiId
            AddDeedTimeSpentEditText.setText(deed.timeSpent.toString())
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