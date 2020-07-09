package com.emtwnty.schemaker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.android.synthetic.main.activity_setting.*

class App: Application() {
    companion object{
        const val CHANNEL_ID_SERVICE = "com.emtwenty.schemaker.service"
        const val CHANNEL_ID_ALARM = "com.emtwenty.schemaker.alarm"
    }
    private lateinit var mSharedPrefSetting: SharedPreferences
    private var ID_PREF_SETTING = "com.emtwnty.schemaker-setting"

    override fun onCreate() {
        super.onCreate()
        createNotification()

        mSharedPrefSetting = getSharedPreferences(ID_PREF_SETTING,Context.MODE_PRIVATE)
        checkSetting()
    }

    private fun checkSetting(){
        val lighMode = mSharedPrefSetting.getBoolean("LIGHMODE",false)
        if(lighMode == false){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
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