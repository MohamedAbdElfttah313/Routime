package com.example.routime.Presentation.UiControllers

import android.app.PendingIntent
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.routime.Constants
import com.example.routime.ExtrasNames
import com.example.routime.Presentation.Services.HourlyReminderService
import com.example.routime.R
import com.example.routime.databinding.ActivityNavigatorMainBinding

class NavigatorMainActivity : AppCompatActivity() {
    private lateinit var xmlView : ActivityNavigatorMainBinding
    private var selectedImageView : ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        xmlView = ActivityNavigatorMainBinding.inflate(layoutInflater)
        setContentView(xmlView.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
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

        supportFragmentManager.beginTransaction().add(R.id.MainScreenFragmentContainerView, TodayDeedFragment()).commit()

        val checkerIntent = Intent(this, HourlyReminderService::class.java).apply {
            putExtra(ExtrasNames.FLAG_SHOULD_SHOW_REMINDER_OVERLAY.name,true)
        }
        val checkerPendingIntent = PendingIntent.getService(this, Constants.PENDING_INTENT_REQUEST_CODE,
            checkerIntent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_NO_CREATE)

        if (checkerPendingIntent!=null){
           println("Pending Intent WIll fire")
        }else {
            println("Pending Intent will not fire")
        }

        val todayDeedFragment = TodayDeedFragment()
        val searchDeedFragment = SearchDeedFragment()

        val settingsFragment = SettingsFragment()
        val fragment = Fragment()
        with(xmlView){
            selectedImageView = TodayIconImageView
            TodayIconImageView.setOnClickListener { startFragment(todayDeedFragment,it as? ImageView) }
            SearchIconImageView.setOnClickListener { startFragment(searchDeedFragment,it as? ImageView) }
            ChartIconImageView.setOnClickListener { startFragment(fragment,it as? ImageView) }
            SettingsIconImageView.setOnClickListener { startFragment(settingsFragment , it as? ImageView) }
        }
    }

    private fun startFragment(fragment : Fragment , imageView : ImageView?){
        updateNavigationBar(imageView!!)
        supportFragmentManager.beginTransaction()
            .replace(R.id.MainScreenFragmentContainerView,fragment)
            .setReorderingAllowed(true)
            .commit()
    }

    private fun updateNavigationBar(imageView : ImageView){
        selectedImageView!!.imageTintList = ColorStateList.valueOf("#6C727E".toColorInt())
        imageView.imageTintList = ColorStateList.valueOf("#FFFFFF".toColorInt())
        selectedImageView = imageView
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

    fun navigateToHomeFragment(){
        xmlView.TodayIconImageView.performClick()
    }
}