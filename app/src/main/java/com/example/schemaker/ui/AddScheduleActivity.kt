package com.example.schemaker.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import com.example.schemaker.R
import kotlinx.android.synthetic.main.activity_add_schedule.*
import java.util.*

class AddScheduleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_schedule)

        val cal = Calendar.getInstance()
        tv_tanggal_addSchedule.text = DateFormat.format("dd MMM yyyy",cal).toString()

        tv_tanggal_addSchedule.setOnClickListener {
            val dateSetListener = DatePickerDialog.OnDateSetListener { datePicker, tahun, bulan, tanggal ->
                cal.set(Calendar.DATE,tanggal)
                cal.set(Calendar.MONTH,bulan)
                cal.set(Calendar.YEAR,tahun)
                tv_tanggal_addSchedule.text = DateFormat.format("dd, MMM yyyy",cal)
            }
            DatePickerDialog(this,R.style.DialogTheme,dateSetListener,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE)).show()
        }

        tv_jam_addSchedule.text = DateFormat.format("HH:mm a",cal).toString()
        tv_jam_addSchedule.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, jam, menit ->
                cal.set(Calendar.HOUR_OF_DAY,jam)
                cal.set(Calendar.MINUTE,menit)
                tv_jam_addSchedule.text = DateFormat.format("HH:mm a",cal).toString()
            }
            TimePickerDialog(this,R.style.DialogTheme,timeSetListener,cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),false).show()
        }
    }
}