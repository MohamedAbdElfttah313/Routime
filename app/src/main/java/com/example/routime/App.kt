package com.example.routime

import android.app.Activity
import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val defaultNotificationChannel = NotificationChannel(Constants.CHANNEL_ID_DEFAULT,
                "APP_DEFAULT_CHANNEL",
                NotificationManager.IMPORTANCE_LOW).apply {
                description = "Default Routime Application Channel To Post Notification"
            }

            val soundNotificationChannel = NotificationChannel(
                Constants.CHANNEL_ID_SOUND,
                "APP_DEFAULT_CHANNEL_WITH_SOUND",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(false)
                description = "Default Routime Application Channel To Post Notification"
            }


            val soundVibratesNotificationChannel =  NotificationChannel(Constants.CHANNEL_ID_SOUND_VIBRATION,
                "APP_DEFAULT_CHANNEL_WITH_SOUND_VIBRATION",
                NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Default Routime Application Channel To Post Notification"
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channels = listOf(defaultNotificationChannel,soundNotificationChannel,soundVibratesNotificationChannel)
            notificationManager.createNotificationChannels(channels)
        }
    }

}