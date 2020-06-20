package com.example.schemaker

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build

/**Halo**/

class AlarmReceiver : BroadcastReceiver() {
    private lateinit var notificationManager : NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var builder : Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Text Notification"
    override fun onReceive(context: Context?, intent: Intent?) {
        notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val wibu = intent?.getStringExtra("title")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(
                channelId,description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(context,channelId)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(wibu)
                .setContentText("bangsat")
        }else{
            builder = Notification.Builder(context)
                .setContentTitle("halo")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentText("Bangsat")
        }
        notificationManager.notify(1234,builder.build())
    }
}