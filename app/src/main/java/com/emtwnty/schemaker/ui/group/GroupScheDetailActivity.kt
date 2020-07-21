package com.emtwnty.schemaker.ui.group

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.viewmodel.GroupScheViewModel
import com.emtwnty.schemaker.viewmodel.GroupViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_group_sche_detail.*
import java.util.*
import kotlin.collections.HashMap

class GroupScheDetailActivity : AppCompatActivity() {

    private lateinit var mGroupViewModel: GroupViewModel
    private lateinit var mGroupScheduleViewModel: GroupScheViewModel
    private lateinit var mAuth: FirebaseAuth

    private lateinit var exScheduleID: String
    private lateinit var exGroupID: String
    private lateinit var exTitleSche: String
    private lateinit var exDescSche: String
    private var exTimestampSche: Long = 0L

    private lateinit var mCalendar: Calendar
    private lateinit var mNewCalendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_sche_detail)

        mCalendar = Calendar.getInstance()
        mNewCalendar = Calendar.getInstance()

        mAuth = FirebaseAuth.getInstance()
        val userID = mAuth.currentUser?.uid.toString()

        // Init ViewModel
        mGroupScheduleViewModel = ViewModelProviders.of(this).get(GroupScheViewModel::class.java)
        mGroupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)

        // Init value dari put extra
        exScheduleID = intent.getStringExtra("SCHEDULE_ID").toString()
        exGroupID = intent.getStringExtra("GROUP_ID").toString()
        exTitleSche = intent.getStringExtra("TITLE").toString()
        exDescSche = intent.getStringExtra("DESC").toString()
        exTimestampSche = intent.getLongExtra("TIMESTAMP",0L)
        updateUI()

        mGroupViewModel.getUserRole(userID,exGroupID).observe(this, Observer {
            if(it == "genin"){
                btn_editGroupSche_groupScheDetail.visibility = View.GONE
            }else btn_editGroupSche_groupScheDetail.visibility = View.VISIBLE
        })

        // Edit Button
        btn_editGroupSche_groupScheDetail.setOnClickListener {
            editable(true)
        }
        // Cancel Button
        btn_cancel_groupScheDetail.setOnClickListener {
            editable(false)
        }
        // Save Button
        btn_save_groupScheDetail.setOnClickListener {
            nullValidation()
        }

        // DatePickerDialog Button
        btn_tanggal_groupScheDetail.setOnClickListener {
            dialogDatePicker()
        }
        // TimePickerDialog Button
        btn_jam_groupScheDetail.setOnClickListener {
            dialogTimePicker()
        }
    }

    private fun dialogTimePicker(){
        val timePickerListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            mNewCalendar.set(Calendar.HOUR,hour)
            mNewCalendar.set(Calendar.MINUTE,minute)

            btn_jam_groupScheDetail.text = DateFormat.format("hh:mm a",mNewCalendar)
        }
        TimePickerDialog(
            this,
            R.style.DialogTheme,
            timePickerListener,
            mNewCalendar.get(Calendar.HOUR),
            mNewCalendar.get(Calendar.MINUTE), false
        ).show()
    }
    private fun dialogDatePicker(){
        val datePickerListener = DatePickerDialog.OnDateSetListener { datePicker, year, month, date ->
            mNewCalendar.set(Calendar.YEAR,year)
            mNewCalendar.set(Calendar.MONTH,month)
            mNewCalendar.set(Calendar.DATE,date)

            btn_tanggal_groupScheDetail.text = DateFormat.format("dd MMM yyyy",mNewCalendar)
        }
        val datePickerDialog = DatePickerDialog(
            this,
            R.style.DialogTheme,
            datePickerListener,
            mNewCalendar.get(Calendar.YEAR),
            mNewCalendar.get(Calendar.MONTH),
            mNewCalendar.get(Calendar.DATE)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    /** [ START ]  Validation  **/
    private fun nullValidation(){
        val et_title = et_title_groupScheDetail.text
        val et_desc = et_description_groupScheDetail.text
        if(et_title.isNullOrEmpty() || et_title.isNullOrBlank()){
            tf_title_groupScheDetail.isErrorEnabled = true
            tf_title_groupScheDetail.error = "Title cannot be empty"
            return
        }
        tf_title_groupScheDetail.isErrorEnabled = false

        if(et_desc.isNullOrEmpty() || et_title.isNullOrBlank()){
            tf_description_groupScheDetail.isErrorEnabled = true
            tf_description_groupScheDetail.error = "Title cannot be empty"
            return
        }
        tf_description_groupScheDetail.isErrorEnabled = false

        similarityValidation(et_title.toString(),et_desc.toString())
    }
    private fun similarityValidation(newTitle: String, newDesc: String){
        val oldTimestemp = mCalendar.timeInMillis / 1000
        val newTimestamp = mNewCalendar.timeInMillis / 1000

        if(oldTimestemp == newTimestamp && exTitleSche == newTitle && exDescSche == newDesc){
            Toast.makeText(this,"You didn't change anything yet", Toast.LENGTH_SHORT).show()
        }
        else{
            val newGroupScheMap = HashMap<String,Any>()
            newGroupScheMap.put("timestamp",newTimestamp)
            newGroupScheMap.put("title",newTitle)
            newGroupScheMap.put("description",newDesc)
            mGroupScheduleViewModel.updateGroupSche(exScheduleID,newGroupScheMap)
            super.onBackPressed()
        }

    }
    /** [ END ]  Validation  **/

    private fun editable(editableCondition: Boolean){
        if(editableCondition == false){
            btn_cancel_groupScheDetail.visibility = View.GONE
            btn_save_groupScheDetail.visibility = View.GONE
            btn_editGroupSche_groupScheDetail.visibility = View.VISIBLE

            et_title_groupScheDetail.isEnabled = false
            et_description_groupScheDetail.isEnabled = false
            btn_tanggal_groupScheDetail.isEnabled = false
            btn_jam_groupScheDetail.isEnabled = false
        }
        else{
            btn_cancel_groupScheDetail.visibility = View.VISIBLE
            btn_save_groupScheDetail.visibility = View.VISIBLE
            btn_editGroupSche_groupScheDetail.visibility = View.GONE

            et_title_groupScheDetail.isEnabled = true
            et_description_groupScheDetail.isEnabled = true
            btn_tanggal_groupScheDetail.isEnabled = true
            btn_jam_groupScheDetail.isEnabled = true
        }
    }

    private fun updateUI(){
        et_title_groupScheDetail.setText(exTitleSche)
        et_description_groupScheDetail.setText(exDescSche)
        mCalendar.timeInMillis = exTimestampSche * 1000L
        mNewCalendar.timeInMillis = exTimestampSche * 1000L

        btn_tanggal_groupScheDetail.text = DateFormat.format("dd MMM yyyy",mCalendar).toString()
        btn_jam_groupScheDetail.text = DateFormat.format("hh:mm a",mCalendar).toString()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}