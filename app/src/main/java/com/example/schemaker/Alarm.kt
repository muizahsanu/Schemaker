package com.example.schemaker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class Alarm {
//    private var alarmManager : AlarmManager? = null
//    private lateinit var alarmIntent : PendingIntent
//    private lateinit var mCalendar: Calendar

//    alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//    val intent = Intent(this, AlarmReceiver::class.java)
//    intent.putExtra("title","Yoman")
//    alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//    initMinDate()
//
//    //set alarm
//    btn.setOnClickListener(){
//        val getDate = datePicker.dayOfMonth
//        val getMonth = datePicker.month
//        val getYear = datePicker.year
//        val calendar: Calendar = Calendar.getInstance().apply {
//            timeInMillis = System.currentTimeMillis()
//            set(Calendar.DATE, getDate)
//            set(Calendar.MONTH, getMonth)
//            set(Calendar.YEAR, getYear)
//        }
//        alarmManager?.setInexactRepeating(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            AlarmManager.INTERVAL_DAY,
//            alarmIntent
//        )
//        Toast.makeText(this,getDate.toString()+"-"+(getMonth+1) +"-"+getYear.toString(), Toast.LENGTH_LONG).show()
//    }

//    private fun initMinDate(){
//        mCalendar = Calendar.getInstance()
//        mCalendar.add(Calendar.DATE, 0)
//        datePicker.minDate = mCalendar.timeInMillis
//    }
}