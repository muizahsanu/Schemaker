package com.example.schemaker

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.text.format.DateFormat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.example.schemaker.App.Companion.CHANNEL_ID_SERVICE
import com.example.schemaker.model.ScheduleEntity
import com.example.schemaker.viewmodel.ScheduleViewModel
import java.util.*

class ScheduleService: Service(),LifecycleOwner {
    private lateinit var mScheduleViewModel: ScheduleViewModel

    private lateinit var lifecycleRegistry: LifecycleRegistry
    private lateinit var scID :String
    private lateinit var scTime: String
    private lateinit var isiIntent:String

    override fun onCreate() {
        super.onCreate()

        scID = ""

        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.markState(Lifecycle.State.CREATED)

        mScheduleViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(ScheduleViewModel::class.java)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isiIntent = intent?.getStringExtra("ASD").toString()

        lifecycleRegistry.markState(Lifecycle.State.STARTED)
        mScheduleViewModel.getRowsSchedule()?.observeForever(Observer<List<ScheduleEntity>>{
            if(it.isNotEmpty()){
                setAlarm(it)
                memek(it)
            }
            else this.stopSelf()
        })

        return START_NOT_STICKY
    }

    private fun memek(scheduleList: List<ScheduleEntity>){
        val scTimestamp = scheduleList.get(0).timestamp.toLong()
        val scTitle = scheduleList.get(0).title
        val scWithTime = scheduleList.get(0).with_time
        var scTitle2 = ""
        var nextEvent = "Nothing"

        val scheDeadline2 = Calendar.getInstance()
        if(scheduleList.size > 1){
            val scTimestamp2 = scheduleList.get(1).timestamp.toLong()
            scTitle2 = scheduleList.get(1).title
            scTitle2 += " - "
            scheDeadline2.timeInMillis = scTimestamp2 * 1000L
            nextEvent = DateFormat.format("MMM dd, yyyy",scheDeadline2).toString()
        }

        val scheDeadline = Calendar.getInstance()
        scheDeadline.timeInMillis = scTimestamp * 1000L
        val currentDate = Calendar.getInstance()

        val jarakTanggal =scheDeadline.get(Calendar.DATE) - currentDate.get(Calendar.DATE)

        if(scWithTime == true){
            if(jarakTanggal == 1){
                scTime = "Tomorrow on "
            }
            scTime += DateFormat.format("MMM dd, yyyy hh:mm a",scheDeadline).toString()
        }else scTime += DateFormat.format("MMM dd, yyyy",scheDeadline).toString()

//        if(jarakTanggal == 1){
//            scTime = "Tomorrow on "
//            if(scWithTime == true){
//                scTime += DateFormat.format("MMM dd, yyyy hh:mm a",scheDeadline).toString()
//            }else scTime += DateFormat.format("MMM dd, yyyy",scheDeadline).toString()
//        }
//        else {
//            scTime = DateFormat.format("MMM dd, yyyy",scheDeadline).toString()
//        }

        val notif = NotificationCompat.Builder(this,CHANNEL_ID_SERVICE)
            .setContentTitle(scTitle+" - "+scTime)
            .setContentText("[Next event]: "+scTitle2+nextEvent)
            .setSmallIcon(R.drawable.ic_calendar)
            .build()
        startForeground(1,notif)
    }

    private fun setAlarm(scheduleList: List<ScheduleEntity>){
        scID = scheduleList.get(0).scheduleID
        val scTitle = scheduleList.get(0).title
        val scTimestamp = scheduleList.get(0).timestamp.toLong()

        val calDeadline = Calendar.getInstance()
        calDeadline.timeInMillis = scTimestamp * 1000L

        val alarmMngr = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val alarmIntent = Intent(this,AlarmReceiver::class.java).let {
            it.putExtra("SC_TIME",scTimestamp)
            it.putExtra("SC_TITLE",scTitle)
            PendingIntent.getBroadcast(this,1,it,PendingIntent.FLAG_UPDATE_CURRENT)
        }

        alarmMngr?.setExact(
            AlarmManager.RTC_WAKEUP,
            calDeadline.timeInMillis,
            alarmIntent
        )

    }

    override fun onDestroy() {
        val alarmMngr = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val alarmIntent = Intent(this,AlarmReceiver::class.java).let {
            PendingIntent.getBroadcast(this,1,it,PendingIntent.FLAG_UPDATE_CURRENT)
        }
        alarmMngr?.cancel(alarmIntent)
        if(scID.isNotEmpty()){
            mScheduleViewModel.updateRemindMe(scID,false)
        }
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }
}