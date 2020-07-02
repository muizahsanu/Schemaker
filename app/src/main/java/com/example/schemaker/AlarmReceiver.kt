package com.example.schemaker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.example.schemaker.App.Companion.CHANNEL_ID_ALARM
import java.util.*

class AlarmReceiver: BroadcastReceiver(), LifecycleOwner {
    override fun onReceive(context: Context?, p1: Intent?) {
        val scTimestamp = p1?.getLongExtra("SC_TIME",0)
        val scTitle = p1?.getStringExtra("SC_TITLE")

        val scDeadline = Calendar.getInstance()
        scDeadline.timeInMillis = scTimestamp!! * 1000L
        val currentDate = Calendar.getInstance()
        currentDate.timeInMillis

        val scDateFormat = DateFormat.format("E, MMMM yyyy",scDeadline)

        val notifAlarm = NotificationCompat.Builder(context!!, CHANNEL_ID_ALARM)
            .setContentTitle("You have an event for today!!!")
            .setContentText(scTitle)
            .setSmallIcon(R.drawable.ic_calendar)
            .build()

        NotificationManagerCompat.from(context).notify(2, notifAlarm)

        val serviceIntent = Intent(context,ScheduleService::class.java)
        context.stopService(serviceIntent)
        context.startService(serviceIntent)
    }

    override fun getLifecycle(): Lifecycle {
        TODO("Not yet implemented")
    }
}