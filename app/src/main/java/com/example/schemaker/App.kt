package com.example.schemaker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.schemaker.viewmodel.ScheduleViewModel

class App: Application() {
    companion object{
        const val CHANNEL_ID_SERVICE = "com.etwenty.schemaker.service"
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
        }
    }
}