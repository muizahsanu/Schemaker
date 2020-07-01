package com.example.schemaker

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.schemaker.App.Companion.CHANNEL_ID_SERVICE
import com.example.schemaker.ui.main.HomeActivity

class ScheduleService: Service() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val scID = intent?.getStringExtra("SC_ID")
        val scTimestamp = intent?.getStringExtra("SC_TIME")
        val scTitle = intent?.getStringExtra("SC_TITLE")

        val notif = NotificationCompat.Builder(this,CHANNEL_ID_SERVICE)
            .setContentTitle(scTitle)
            .setContentText(scTimestamp)
            .setSmallIcon(R.drawable.ic_calendar)
            .build()

        startForeground(1,notif)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}