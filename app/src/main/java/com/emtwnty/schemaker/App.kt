package com.emtwnty.schemaker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class App: Application() {
    companion object{
        const val CHANNEL_ID_SERVICE = "com.emtwenty.schemaker.service"
        const val CHANNEL_ID_ALARM = "com.emtwenty.schemaker.alarm"
    }

    override fun onCreate() {
        super.onCreate()
        createNotification()
    }

    private fun createNotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val serviceChannel = NotificationChannel(
                CHANNEL_ID_SERVICE,
                "Example Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notifManager.createNotificationChannel(serviceChannel)

            val alarmChannel = NotificationChannel(
                CHANNEL_ID_ALARM,
                "Example Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val alarmManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            alarmManager.createNotificationChannel(alarmChannel)
        }
    }
}