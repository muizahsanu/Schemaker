package com.emtwnty.schemaker.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.RadioGroup
import android.widget.TimePicker
import androidx.lifecycle.ViewModelProviders
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.model.ScheduleEntity
import com.emtwnty.schemaker.ui.main.HomeActivity
import com.emtwnty.schemaker.viewmodel.ScheduleViewModel
import kotlinx.android.synthetic.main.activity_add_schedule.*
import java.util.*

class AddScheduleActivity : AppCompatActivity() {

    private lateinit var mColorPick: String
    private var withTime: Boolean = true
    private lateinit var mScheduleViewMode: ScheduleViewModel
    private lateinit var mCalendar: Calendar
    private var editCheck: Boolean = false
    private var remindMe: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_schedule)

        // Mendeklarasikan ViewModel
        mScheduleViewMode = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)

        // Mengambil tanggal dan jam sekarang atau current date time
        mCalendar = Calendar.getInstance()
        mCalendar.add(Calendar.DATE,1)

        val scID = intent.getStringExtra("SC_ID")
        if(scID != null){
            updateUI()
        }
        else{
            cb_rimendMe_addSchedule.isChecked = true
            tv_tanggal_addSchedule.text = DateFormat.format("dd MMM yyyy",mCalendar).toString()
            mCalendar.apply {
                this.set(Calendar.HOUR, 7)
                this.set(Calendar.MINUTE,0)
                this.set(Calendar.SECOND,0)
                this.set(Calendar.AM_PM,Calendar.AM)
            }
        }

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
        cb_rimendMe_addSchedule.setOnCheckedChangeListener { button, isChecked ->
            remindMe = isChecked
        }

        btn_save_addSchedule.setOnClickListener {
            if(scID != null && editCheck == false){
                editCheck = true
                updateUI()
            }
            else if(scID != null && editCheck == true){
                // update data
                setDataToRoomDb(scID)
                updateUI()
            }
            else{
                val newSCID = UUID.randomUUID().toString()
                formValidation(newSCID)
            }
        }

        btn_cancel_addSchedule.setOnClickListener {
            editCheck = false
            updateUI()
        }

        /**
         * -------------------------BAGIAN PEMILIHAN TANGGAL---------------------------
         * **/
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

    private fun updateUI(){
        val scTitle = intent.getStringExtra("SC_TITLE")
        val scDesc = intent.getStringExtra("SC_DESC")
        val scTime = intent.getStringExtra("SC_TIME")
        val scBgColor = intent.getStringExtra("SC_BGCOLOR")
        val scWithTime = intent.getBooleanExtra("SC_WITHTIME",false)
        val scRemindMe = intent.getBooleanExtra("SC_REMINDME",false)

        toolbar_addSchedule.setBackgroundColor(Color.parseColor(scBgColor))
        btn_save_addSchedule.setBackgroundColor(Color.parseColor(scBgColor))
        batas1_addSchedule.setBackgroundColor(Color.parseColor(scBgColor))
        batas2_addSchedule.setBackgroundColor(Color.parseColor(scBgColor))
        batas3_addSchedule.setBackgroundColor(Color.parseColor(scBgColor))
        batas4_addSchedule.setBackgroundColor(Color.parseColor(scBgColor))

        remindMe = scRemindMe
        when(remindMe){
            false->cb_rimendMe_addSchedule.isChecked = false
            true->cb_rimendMe_addSchedule.isChecked = true
        }

        mCalendar.timeInMillis = scTime!!.toLong() * 1000L

        withTime = scWithTime
        if(withTime == false){
            tv_jam_addSchedule.visibility = View.INVISIBLE
            swButton_time_addSchedule.isChecked = true
        }
        else{
            tv_jam_addSchedule.visibility = View.VISIBLE
            swButton_time_addSchedule.isChecked = false
        }

        et_title_addSchedule.setText(scTitle.toString())
        et_description_addSchedule.setText(scDesc.toString())
        tv_tanggal_addSchedule.text = DateFormat.format("dd MMM yyyy",mCalendar).toString()
        tv_jam_addSchedule.text = DateFormat.format("hh:mm a",mCalendar).toString()

        if(editCheck == true){
            et_title_addSchedule.isEnabled = true
            et_description_addSchedule.isEnabled = true
            tv_tanggal_addSchedule.isEnabled = true
            tv_jam_addSchedule.isEnabled = true
            cb_rimendMe_addSchedule.isEnabled = true
            lin_withTime_addSchedule.visibility = View.VISIBLE
            radioGroup_color_addSchedule.visibility = View.VISIBLE
            btn_save_addSchedule.text = "Save"
            btn_cancel_addSchedule.visibility = View.VISIBLE
        }
        else{
            et_title_addSchedule.isEnabled = false
            et_description_addSchedule.isEnabled = false
            tv_tanggal_addSchedule.isEnabled = false
            tv_jam_addSchedule.isEnabled = false
            cb_rimendMe_addSchedule.isEnabled = false
            lin_withTime_addSchedule.visibility = View.GONE
            radioGroup_color_addSchedule.visibility = View.GONE
            btn_save_addSchedule.text = "Edit"
            btn_cancel_addSchedule.visibility = View.GONE
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


    private fun formValidation(scheduleID:String){
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

        setDataToRoomDb(scheduleID)
    }


    private fun setDataToRoomDb(scheduleID:String){
        if(tv_jam_addSchedule.visibility == View.INVISIBLE){
            mCalendar.apply {
                this.set(Calendar.HOUR,0)
                this.set(Calendar.MINUTE,0)
                this.set(Calendar.SECOND,0)
            }
        }
        val title = et_title_addSchedule.text.toString()
        val description = et_description_addSchedule.text.toString()
        val timestamp = mCalendar.timeInMillis / 1000
        val bgColor = mColorPick
        val with_time = withTime
        val remindMe = remindMe
        val done = true
        val schedules = ScheduleEntity(scheduleID,title,description,timestamp.toString(),bgColor,with_time,remindMe,done)

        mScheduleViewMode.setData(schedules)
        Log.w("AddSche_Result",schedules.toString())
        val intent = Intent(this,HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

}