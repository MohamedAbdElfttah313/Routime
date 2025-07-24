package com.example.routime.Presentation.Services

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PixelFormat
import android.os.SystemClock
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.example.routime.Constants
import com.example.routime.ExtrasNames
import com.example.routime.Presentation.UiControllers.AddNewDeedActivity
import com.example.routime.R
import com.example.routime.Utils
import java.util.Calendar
import java.util.Date

class HourlyReminderService : Service() {

    private lateinit var windowManager : WindowManager
    private lateinit var alarmManager : AlarmManager
    private lateinit var sharedPreference : SharedPreferences
    private var reminderOverlayView : View? = null
    var counterTest = 666

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        sharedPreference = getSharedPreferences(Constants.SHARED_PREF_NAME,Activity.MODE_PRIVATE)


        val checkerIntent = Intent(this,HourlyReminderService::class.java).apply {
            putExtra(ExtrasNames.FLAG_SHOULD_SHOW_REMINDER_OVERLAY.name,true)
        }
        val checkerPendingIntent = PendingIntent.getService(this,Constants.PENDING_INTENT_REQUEST_CODE,
            checkerIntent,PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_NO_CREATE)

        if (checkerPendingIntent!=null){
            println("Upcoming Alarm is cancelled")
            PendingIntent.getService(this,Constants.PENDING_INTENT_REQUEST_CODE,
                checkerIntent,PendingIntent.FLAG_MUTABLE).also(alarmManager::cancel)
        }

        val soundEnabled = sharedPreference.getBoolean(Constants.REMINDER_NOTIFICATION_SOUND,false)
        val vibrationEnabled = sharedPreference.getBoolean(Constants.REMINDER_NOTIFICATION_VIBRATE,false)

        val (notificationImportance,channelId) = when(true){
            (soundEnabled && vibrationEnabled) -> Pair(NotificationCompat.PRIORITY_HIGH,Constants.CHANNEL_ID_SOUND_VIBRATION)
            (soundEnabled) -> Pair( NotificationCompat.PRIORITY_HIGH,Constants.CHANNEL_ID_SOUND)
            else -> Pair( NotificationCompat.PRIORITY_LOW,Constants.CHANNEL_ID_DEFAULT)
        }
        counterTest = sharedPreference.getInt("TEST_COUNTER",666).also(::println)
        val reminderNotification = NotificationCompat.Builder(this , channelId)
            .setContentTitle("Routime")
            .setContentText("counter : $counterTest ,Your Brave Reminder Will Always Be Here For you")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(notificationImportance)
            .build()

        startForeground(Constants.REMINDER_SERVICE_REQUEST_CODE,reminderNotification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        removeOverlayIfPreviousStillShown()
        val reminderEnabled = sharedPreference.getBoolean(Constants.REMINDER_ENABLED,false)
        if (reminderEnabled){
            val shouldShowReminderOverlay = intent?.getBooleanExtra(ExtrasNames.FLAG_SHOULD_SHOW_REMINDER_OVERLAY.name,false)?:false
            if (intent!=null&&shouldShowReminderOverlay) drawReminderOverlayView()

            val reminderServicePendingIntent = Intent(this,HourlyReminderService::class.java).run {
                putExtra(ExtrasNames.FLAG_SHOULD_SHOW_REMINDER_OVERLAY.name,true)
                PendingIntent.getService(this@HourlyReminderService,Constants.PENDING_INTENT_REQUEST_CODE,
                    this,PendingIntent.FLAG_MUTABLE)
            }

            val calender = Calendar.getInstance().apply {

                val isReminderWeekdaysOnly = sharedPreference.getBoolean(Constants.REMINDER_WEEKDAYS_ONLY,false)
                val reminderFrequencyInHours = sharedPreference.getInt(Constants.REMINDER_FREQUENCY,1)
                val reminderWorkingIntervalStartTimeHour = sharedPreference.getInt(Constants.REMINDER_START_TIME,0)
                val reminderWorkingIntervalEndTimeHour = sharedPreference.getInt(Constants.REMINDER_END_TIME,24)
                add(Calendar.HOUR_OF_DAY,reminderFrequencyInHours)
                if (get(Calendar.HOUR_OF_DAY) !in reminderWorkingIntervalStartTimeHour..reminderWorkingIntervalEndTimeHour){
                    val hoursUntilNextReminder = ((24-get(Calendar.HOUR_OF_DAY)%24) + reminderWorkingIntervalStartTimeHour)
                    add(Calendar.HOUR_OF_DAY,(hoursUntilNextReminder))
                }

                if (isReminderWeekdaysOnly && get(Calendar.DAY_OF_WEEK) in arrayOf(0,6)){
                    val daysUntilNextReminder = if (get(Calendar.DAY_OF_WEEK )==6) 2 else 1
                    add(Calendar.DAY_OF_MONTH,daysUntilNextReminder)
                    set(Calendar.HOUR_OF_DAY,reminderWorkingIntervalStartTimeHour)
                    set(Calendar.MINUTE,0)
                    set(Calendar.SECOND,0)

                }

            }

            alarmManager.set(AlarmManager.RTC, calender.timeInMillis, reminderServicePendingIntent)
            sharedPreference.edit().putInt("TEST_COUNTER",counterTest+1).apply()
            if (!shouldShowReminderOverlay) stopSelf()


        }else{
            stopSelf()
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?) = null

    override fun onDestroy() {
        reminderOverlayView?.let {
            windowManager.removeView(it)
        }
        super.onDestroy()
    }

    private fun removeOverlayIfPreviousStillShown(){
        if (reminderOverlayView!=null) windowManager.removeView(reminderOverlayView)
    }


    private fun drawReminderOverlayView(){
        val inflater =  getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (checkCanDrawOverlayPermission()){
            val (overlayView,params) = getReminderOverlayViewAndParams(inflater)
            windowManager.addView(overlayView,params)
        }
    }

    private fun checkCanDrawOverlayPermission() = Settings.canDrawOverlays(this)


    private fun getReminderOverlayViewAndParams(inflater : LayoutInflater):Pair<View,LayoutParams>{
        reminderOverlayView = inflater.inflate(R.layout.reminder_overlay,null)
        val params = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            LayoutParams.TYPE_APPLICATION_OVERLAY,
            LayoutParams.FLAG_NOT_FOCUSABLE or LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or LayoutParams.FLAG_TURN_SCREEN_ON,
            PixelFormat.TRANSLUCENT).apply {
            gravity = Gravity.CENTER
        }

        (reminderOverlayView as View).findViewById<TextView>(R.id.tv_current_time)
            .text = Utils.formatTimeForDataBase(Date(),"HH:mm")

        (reminderOverlayView as View).findViewById<AppCompatButton>(R.id.btn_later).setOnClickListener {
            stopSelf()
        }

        (reminderOverlayView as View).findViewById<AppCompatButton>(R.id.btn_add_deed).setOnClickListener {
            stopSelf()
            Intent(this,AddNewDeedActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }.also(::startActivity)
        }
        return Pair(reminderOverlayView!! , params)

    }
}