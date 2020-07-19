package com.emtwnty.schemaker.ui.group

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.model.online.GroupScheModel
import com.emtwnty.schemaker.viewmodel.GroupScheViewModel
import kotlinx.android.synthetic.main.activity_add_sche_online.*
import java.util.*

class AddScheOnlineActivity : AppCompatActivity() {

    private lateinit var groupID: String
    private lateinit var mCalendar: Calendar
    private lateinit var mGroupScheViewModel: GroupScheViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_sche_online)

        // Get Group ID from intent extra
        groupID = intent.getStringExtra("GROUP_ID").toString()

        mCalendar = Calendar.getInstance()

        mGroupScheViewModel = ViewModelProviders.of(this).get(GroupScheViewModel::class.java)


        /** [ START ] Inisialisasi awal tanggal dan jam **/
        // Tanggal +1 dari tanggal saat ini
        mCalendar.add(Calendar.DATE, 1)
        // Set jam
        mCalendar.apply {
            set(Calendar.HOUR, 8)
            set(Calendar.MINUTE,0)
            set(Calendar.SECOND,0)
            set(Calendar.AM_PM,Calendar.AM)
        }
        // Update ui Tanggal dan Jam
        btn_jam_addOnlineSche.text = DateFormat.format("hh:mm a",mCalendar).toString()
        btn_tanggal_addOnlineSche.text = DateFormat.format("dd MMM yyyy",mCalendar).toString()
        /** [ END ] Inisialisasi awal tanggal dan jam **/


        btn_tanggal_addOnlineSche.setOnClickListener {
            onDateSetListener()
        }
        btn_jam_addOnlineSche.setOnClickListener {
            onTimeSetListener()
        }
        btn_create_addOnlineSche.setOnClickListener {
            validationForm()
        }

        val asd = intent.getSerializableExtra("asd")
    }

    private fun onDateSetListener() {
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                mCalendar.set(Calendar.YEAR, i)
                mCalendar.set(Calendar.MONTH, i2)
                mCalendar.set(Calendar.DATE, i3)

                btn_tanggal_addOnlineSche.text =
                    DateFormat.format("dd MMM yyyy", mCalendar).toString()
            },
            mCalendar.get(Calendar.YEAR),
            mCalendar.get(Calendar.MONTH),
            mCalendar.get(Calendar.DATE)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun onTimeSetListener(){
        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { timePicker, i, i2 ->
                mCalendar.set(Calendar.HOUR,i)
                mCalendar.set(Calendar.MINUTE,i)

                btn_jam_addOnlineSche.text = DateFormat.format("hh:mm a",mCalendar).toString()
            },
            mCalendar.get(Calendar.HOUR),
            mCalendar.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

    private fun validationForm(){
        val et_title = et_title_addOnlineSche.text
        val et_desc = et_description_addOnlineSche.text
        if(et_title.isNullOrEmpty()){
            tf_title_addOnlineSche.isErrorEnabled = true
            tf_title_addOnlineSche.error = "Title cannot be empty"
            return
        }
        tf_title_addOnlineSche.isErrorEnabled = false

        if(et_desc.isNullOrEmpty()){
            tf_description_addOnlineSche.isErrorEnabled = true
            tf_description_addOnlineSche.error = "Title cannot be empty"
            return
        }
        tf_description_addOnlineSche.isErrorEnabled = false

        addGroupSchedule(et_title.toString(),et_desc.toString())
    }

    private fun addGroupSchedule(title:String, desc:String){
        val scheduleID = UUID.randomUUID().toString()
        val timestamp = mCalendar.timeInMillis / 1000L
        val done = false

        val scheduleData = GroupScheModel(scheduleID,title,desc,timestamp,groupID,done)
        mGroupScheViewModel.addGroupSchedule(scheduleData)
        indicator()
    }

    private fun indicator(){
        mGroupScheViewModel.scheduleResponseCallback().observe(this, Observer<String>{
            when(it){
                "ADDSCEDULE_PROCCESSING"->{
                    Toast.makeText(this,"Add schedule success",Toast.LENGTH_SHORT).show()
                    super.onBackPressed()
                }
                "ADDSCEDULE_SUCCESS"->{

                }
                "ADDSCEDULE_FAILED"->{

                }
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}