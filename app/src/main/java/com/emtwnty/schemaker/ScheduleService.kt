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

        // Memberikan nilai awal untuk scID
        scID = ""

        /**
         * Mendaftarkan LifeCycle pada class service ini
         * karena di class service tidak bisa nge get LifeCycle
         *
         * **/
        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.markState(Lifecycle.State.CREATED)

        // Mendeklarasikan Schedule View Model
        mScheduleViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(ScheduleViewModel::class.java)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isiIntent = intent?.getStringExtra("ASD").toString()

        lifecycleRegistry.markState(Lifecycle.State.STARTED)
        mScheduleViewModel.getRowsSchedule()?.observeForever(Observer<List<ScheduleEntity>>{
            /** --Pengecekan apakah ada schedule yang akan diingatkan pada user--
             *
             * Ini agar forground service tidak dijalankan jika tidak ada
             * list schedule yg harus di ingatkan **/
            // Jika ya maka akan set Alarm sesuai dengan tanggal yg sudah di tetepkan
            // Lalu menjalankan forground service
            if(it.isNotEmpty()){
                // Memanggil fungsi setAlarm lalu mengisi parameter dengan List<ScheduleEntity>
                // yang diambil dari room database
                setAlarm(it)

                // Memanggil fungsi service notifikasi untuk menjalankan foreground service
                // dan memunculkan notifikasi bahwa service sedang berjalan
                notifService(it)
            }
            // Jika tidak maka foreground service akan berhenti
            else this.stopSelf() // Memberhentikan foreground service
        })

        return START_NOT_STICKY
    }

/**
 * ============== Bagian menjalankan Service dan menyeting Notication =======================
 *
 * **/
    private fun notifService(scheduleList: List<ScheduleEntity>){
        val scTimestamp = scheduleList.get(0).timestamp.toLong()
        val scTitle = scheduleList.get(0).title
        val scWithTime = scheduleList.get(0).with_time
        var scTitle2 = ""
        var nextEvent = "Nothing"

        // Set next event atau schedule
        // yang nanti akan dimunculkan di dalam notification service
        val scheDeadline2 = Calendar.getInstance()
        if(scheduleList.size > 1){
            val scTimestamp2 = scheduleList.get(1).timestamp.toLong()
            scTitle2 = scheduleList.get(1).title
            scTitle2 += " - "
            scheDeadline2.timeInMillis = scTimestamp2 * 1000L
            nextEvent = DateFormat.format("MMM dd, yyyy",scheDeadline2).toString()
        }

        // Mengambil tanggal sesuai dengan timestamp yang ada pada schedule di db
        val scheDeadline = Calendar.getInstance()
        scheDeadline.timeInMillis = scTimestamp * 1000L
        // Mengambil tanggal sekarang / current date time
        val currentDate = Calendar.getInstance()

        /**
         *  Pengecekkan apakah Deadline kurang dari satu hari dari Current Date
         *
         * **/
        // Jika ya maka akan mengubah text pada tanggal yg akan ditampilkan
        val jarakTanggal =scheDeadline.get(Calendar.DATE) - currentDate.get(Calendar.DATE)
        if(jarakTanggal == 1){
            scTime = "Tomorrow on "
            // Pengecekan apakah User membuat Schedule dengan waktu yg spesifik
            // Jika ya maka akan memunculkan jam pada service notifikasi
            if(scWithTime == true){
                scTime += DateFormat.format("MMM dd, yyyy hh:mm a",scheDeadline).toString()
            }else scTime += DateFormat.format("MMM dd, yyyy",scheDeadline).toString()
        }
        else {
            scTime = DateFormat.format("MMM dd, yyyy",scheDeadline).toString()
        }

        /**
         *  Setup notification untur service. notifikasi akan muncul jika service berjalan
         *
         * **/
        val notif = NotificationCompat.Builder(this,CHANNEL_ID_SERVICE)
            .setContentTitle(scTitle+" - "+scTime)
            .setContentText("[Next event]: "+scTitle2+nextEvent)
            .setSmallIcon(R.drawable.ic_calendar)
            .build()

        // start or run the foreground service
        // notifikasi akan muncul dan tidak dapat di swap atau dihapus
        // jika foreground service sedang berjalan
        startForeground(1,notif)
    }

/**
 * =============================== Bagian menyetel alarm =====================================
 *
 * **/
    private fun setAlarm(scheduleList: List<ScheduleEntity>){
        scID = scheduleList.get(0).scheduleID
        val scTitle = scheduleList.get(0).title
        val scTimestamp = scheduleList.get(0).timestamp.toLong()

        /**
         * Mengambil date time sesuai dengan timestamp lalu dimasukan ke
         * value calDeadline yang nantinya akan digunakan untuk men-trigger Alarm Manager
         *
         * **/
        val calDeadline = Calendar.getInstance()
        calDeadline.timeInMillis = scTimestamp * 1000L

        /**
         * SETUP Alarm Manager, Alarm intent dan juga Pending Intent
         *
         * **/
        val alarmMngr = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        // membuat intent yang ditujukan ke AlarmReceiver
        val alarmIntent = Intent(this,AlarmReceiver::class.java).let {
            // Menyisipkan data pada intent dan mengirimkannya ke class tujuan
            it.putExtra("SC_TIME",scTimestamp)
            it.putExtra("SC_TITLE",scTitle)

            // Pending intent yang digunakan untuk menjalankan Broadcast
            PendingIntent.getBroadcast(this,1,it,PendingIntent.FLAG_UPDATE_CURRENT)
        }

        /**
         * Setting Alarm Manager
         * yang gunakan untuk men-trigger sesuai tanggal / jam yang sudah di tentukan
         *
         * **/
        // Trigger alarm pada tanggal atau jam yang tepat (Tidak berulang)
        alarmMngr?.setExact(
            // Tipe alarm manager yang digunakan, menggunakan Local Date Time
            AlarmManager.RTC_WAKEUP,
            // Menyetel pada saat kapan Alarm akan di trigger (bisa tanggal/jam)
            calDeadline.timeInMillis,
            // Akan intent kemana jika Alarm sudah di trigger
            alarmIntent
        )

    }

    /**
     * Kondisi ketika Service diberhentikan
     *
     * **/
    override fun onDestroy() {
        /**
         * ====== Ketika Serice berhenti maka memberhentikan Alarm ======
         *
         * **/
        val alarmMngr = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val alarmIntent = Intent(this,AlarmReceiver::class.java).let {
            PendingIntent.getBroadcast(this,1,it,PendingIntent.FLAG_UPDATE_CURRENT)
        }
        alarmMngr?.cancel(alarmIntent)

        /**
         * ===== Ketika service berhenti maka mengubah remindMe menjadi false =======
         *
         * yang dimana Schedule ini tidak akan di remind lagi dan akan masuk
         * kedalam list Schedule yang sudah kelewat atau sudah di di remind
         *
         * **/
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