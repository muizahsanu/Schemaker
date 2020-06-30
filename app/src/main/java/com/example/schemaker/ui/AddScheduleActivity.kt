package com.example.schemaker.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.RadioGroup
import android.widget.TimePicker
import androidx.lifecycle.ViewModelProviders
import com.example.schemaker.R
import com.example.schemaker.model.ScheduleEntity
import com.example.schemaker.ui.main.HomeActivity
import com.example.schemaker.viewmodel.ScheduleViewModel
import kotlinx.android.synthetic.main.activity_add_schedule.*
import java.util.*

class AddScheduleActivity : AppCompatActivity() {

    private lateinit var mColorPick: String
    private var withTime: Boolean = true
    private lateinit var mScheduleViewMode: ScheduleViewModel
    private lateinit var mCalendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_schedule)



        // Mendeklarasikan ViewModel
        mScheduleViewMode = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)

        // Mengambil tanggal dan jam sekarang atau current date time
        mCalendar = Calendar.getInstance()

        // Setup radio button checked change listener
        radioGroup_color_addSchedule.setOnCheckedChangeListener(radioButtonCheckedListener())
        radioButton1.isChecked = true

        swButton_time_addSchedule.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                tv_jam_addSchedule.visibility = View.INVISIBLE
                withTime = false
            }else {
                withTime = true
                tv_jam_addSchedule.visibility = View.VISIBLE
            }
        }

        btn_save_addSchedule.setOnClickListener {
            formValidation()
        }

        /**
         * -------------------------BAGIAN PEMILIHAN TANGGAL---------------------------
         * **/
        // Menampilkan tanggal sekarang / current date
        tv_tanggal_addSchedule.text = DateFormat.format("dd MMM yyyy",mCalendar).toString()
        tv_tanggal_addSchedule.setOnClickListener {

            //Membuat Date Picker
            val datePickerDialog = DatePickerDialog(
                this,
                R.style.DialogTheme,
                onDateSetListener(),
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DATE)
            )

            // Me-limit tanggal (disable tanggal dari current date kebelakang)
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000

            // Menampilkan date picker
            datePickerDialog.show()
        }

        /**
         * --------------------BAGIAN PEMILIHAN JAM / TIME PICKER--------------------------
         * **/
        mCalendar.apply {
            this.set(Calendar.HOUR, 7)
            this.set(Calendar.MINUTE,0)
            this.set(Calendar.SECOND,0)
        }
        tv_jam_addSchedule.text = DateFormat.format("hh:mm a",mCalendar).toString()
        tv_jam_addSchedule.setOnClickListener {
            //
            // Membuat Time Picker Dialog dan memunculkannya
            //
            TimePickerDialog(
                this, //context
                R.style.DialogTheme, // Tema Time Picker Dialog
                onTimeSetListener(), // Listener Time Picker Dialog
                mCalendar.get(Calendar.HOUR), // Menyeting Default Hour pada Time Picker
                mCalendar.get(Calendar.MINUTE), // Menyeting Default Minute pada Time Picker
                false // Tipe jam 12 atau 24
            ).show()
        }
    }

    /**
     * -----------------DIALOG TIME PICKER LISTENER------------------
     * Listener ketika user mimilih waktu (jam/menit) pada Time Picker
     * **/
    private fun onTimeSetListener() = object : TimePickerDialog.OnTimeSetListener {
        override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
            /** p1 = jam. p2 = menit **/
            // Set waktu/time pada mCalendar dengan waktu yang sudah dipilih di timepicker
            mCalendar.set(Calendar.HOUR, p1)
            mCalendar.set(Calendar.MINUTE, p2)

            // Update ui TextView pada kalendar yang waktunya sudah dipilih dari timepicker
            tv_jam_addSchedule.text = DateFormat.format("hh:mm a", mCalendar).toString()
        }
    }



    /**
     * -----------------DIALOG DATE PICKER LISTENER------------------
     * Listener Ketika user memilih tanggal yang ada pada date picker
     * **/
    private fun onDateSetListener() = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
            /** p1: Tahun. p2: Bulan. p3: Tanggal **/
            // Set tanggal pada mCalendar dengan tanggal yang sudah di pilih di date picker
            mCalendar.set(Calendar.YEAR, p1)
            mCalendar.set(Calendar.MONTH, p2)
            mCalendar.set(Calendar.DATE, p3)

            // update ui Text View pada
            tv_tanggal_addSchedule.text = DateFormat.format("dd MMM yyyy",mCalendar).toString()
        }
    }



    /**
     *  ---------------------Listener Radio Group--------------------------
     *  **/
    private fun radioButtonCheckedListener() = object : RadioGroup.OnCheckedChangeListener{
        @SuppressLint("ResourceType")
        override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
            when(p1){
                R.id.radioButton1 ->{
                    mColorPick = resources.getString(R.color.colorBackgroundSchedule1)
                    Log.w("COLOR",mColorPick)
                }
                R.id.radioButton2 ->{
                    mColorPick = resources.getString(R.color.colorBackgroundSchedule2)
                    Log.w("COLOR",mColorPick)
                }
                R.id.radioButton3 ->{
                    mColorPick = resources.getString(R.color.colorBackgroundSchedule3)
                    Log.w("COLOR",mColorPick)
                }
                R.id.radioButton4 ->{
                    mColorPick = resources.getString(R.color.colorBackgroundSchedule4)
                    Log.w("COLOR",mColorPick)
                }
            }
        }
    }


    private fun formValidation(){
        val title = et_title_addSchedule.text
        val description = et_description_addSchedule.text
        if(title.isNullOrBlank()){
            tf_title_addSchedule.isErrorEnabled = true
            tf_title_addSchedule.error = "Title field must not be null"
            return
        }
        tf_title_addSchedule.isErrorEnabled = false

        if(description.isNullOrBlank()){
            tf_description_addSchedule.isErrorEnabled = true
            tf_description_addSchedule.error = "Desctiption must not be null"
            return
        }
        tf_description_addSchedule.isErrorEnabled = false

        setDataToRoomDb()
    }


    private fun setDataToRoomDb(){
        if(tv_jam_addSchedule.visibility == View.INVISIBLE){
            mCalendar.apply {
                this.set(Calendar.HOUR,0)
                this.set(Calendar.MINUTE,0)
                this.set(Calendar.SECOND,0)
            }
        }
        val scheduleID = UUID.randomUUID().toString()
        val title = et_title_addSchedule.text.toString()
        val description = et_description_addSchedule.text.toString()
        val timestamp = mCalendar.timeInMillis / 1000
        val bgColor = mColorPick
        val with_time = withTime
        val schedules = ScheduleEntity(scheduleID,title,description,timestamp.toString(),bgColor,with_time)

        mScheduleViewMode.setData(schedules)
        val intent = Intent(this,HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

}