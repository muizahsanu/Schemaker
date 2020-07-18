package com.emtwnty.schemaker

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.text.format.DateFormat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.emtwnty.schemaker.App.Companion.CHANNEL_ID_SERVICE
import com.emtwnty.schemaker.model.ScheduleEntity
import com.emtwnty.schemaker.viewmodel.GroupScheViewModel
import com.emtwnty.schemaker.viewmodel.ScheduleViewModel
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

        /** [ Mengambil task yang belum selesai ] **/
        mScheduleViewModel.getNotDoneTask()?.observeForever(Observer<List<ScheduleEntity>>{
            if(it.isNotEmpty()){
                setAlarm(it)
                notifService(it)
            }
            else this.stopSelf()
        })

        return START_NOT_STICKY
    }

    /** [START-- setting notification, start service] **/
    private fun notifService(scheduleList: List<ScheduleEntity>){

        /** Data task pertama **/
        val scTimestamp = scheduleList[0].timestamp.toLong()
        val scTitle = scheduleList[0].title
        val scWithTime = scheduleList[0].with_time
        var nextEvent = "Nothing"


        /** Data task yang akan datang dari yang pertama **/
        var scTitle2 = ""
        val scheDeadline2 = Calendar.getInstance()
        if(scheduleList.size > 1){
            val scTimestamp2 = scheduleList[1].timestamp.toLong()
            scTitle2 = scheduleList[1].title
            scTitle2 += " - "
            scheDeadline2.timeInMillis = scTimestamp2 * 1000L
            nextEvent = DateFormat.format("MMM dd, yyyy",scheDeadline2).toString()
        }

        val scheDeadline = Calendar.getInstance()
        scheDeadline.timeInMillis = scTimestamp * 1000L
        val currentDate = Calendar.getInstance()

        val jarakTanggal =scheDeadline.get(Calendar.DATE) - currentDate.get(Calendar.DATE)
        if(jarakTanggal == 1){
            scTime = "Tomorrow on "
            if(scWithTime == true){
                scTime += DateFormat.format("MMM dd, yyyy hh:mm a",scheDeadline).toString()
            }else scTime += DateFormat.format("MMM dd, yyyy",scheDeadline).toString()
        }
        else {
            scTime = DateFormat.format("MMM dd, yyyy",scheDeadline).toString()
        }


        /** Membuat notification **/
        val notif = NotificationCompat.Builder(this,CHANNEL_ID_SERVICE)
            .setContentTitle("$scTitle - $scTime")
            .setContentText("[Next event]: $scTitle2$nextEvent")
            .setSmallIcon(R.drawable.ic_calendar)
            .build()

        /** Menjalankan service di foreground **/
        startForeground(1,notif)
    }
    /** [END-- setting notification, start service] **/


    /** [START-- menyetel alarm manager dan intent] **/
    private fun setAlarm(scheduleList: List<ScheduleEntity>){
        scID = scheduleList[0].scheduleID
        val scTitle = scheduleList[0].title
        val scTimestamp = scheduleList[0].timestamp.toLong()
        val scRemindme = scheduleList[0].remindMe

        /** convert timestamp to date time **/
        val calDeadline = Calendar.getInstance()
        calDeadline.timeInMillis = scTimestamp * 1000L

        /** jika karen date time sudah melebihi deadline **/
        if(calDeadline.before(Calendar.getInstance())){
            mScheduleViewModel.updateRemindMe(scID,false)
        }

        /** Menyeting pending intent **/
        val alarmMngr = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val alarmIntent = Intent(this,AlarmReceiver::class.java).let {
            it.putExtra("SC_TIME",scTimestamp)
            it.putExtra("SC_TITLE",scTitle)
            it.putExtra("SC_REMINDME",scRemindme)

            PendingIntent.getBroadcast(this,1,it,PendingIntent.FLAG_UPDATE_CURRENT)
        }

        /** Menyeting alarm manager (tidak berulang) **/
        alarmMngr?.setExact(
            AlarmManager.RTC_WAKEUP,
            calDeadline.timeInMillis,
            alarmIntent
        )
    }
    /** [END-- menyetel alarm manager dan intent] **/


    /** [START-- Kondisi ketika service berhenti] **/
    override fun onDestroy() {

        /** memberhentikan alarm **/
        val alarmMngr = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val alarmIntent = Intent(this,AlarmReceiver::class.java).let {
            PendingIntent.getBroadcast(this,1,it,PendingIntent.FLAG_UPDATE_CURRENT)
        }
        alarmMngr?.cancel(alarmIntent)

        /** jika Schedule id tidak kosong:
         *  memberikan kondisi bahwa schedule sudah beres atau sudah lewat tanggal **/
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