package com.emtwnty.schemaker.ui.main

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emtwnty.schemaker.AlarmReceiver
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.ScheduleService
import com.emtwnty.schemaker.adapter.ScheduleAdapter
import com.emtwnty.schemaker.adapter.ScheduleDoneAdapter
import com.emtwnty.schemaker.model.ScheduleEntity
import com.emtwnty.schemaker.ui.AddScheduleActivity
import com.emtwnty.schemaker.viewmodel.ScheduleViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), ScheduleAdapter.ItemClickListener,ScheduleDoneAdapter.ItemClickListener {

    private var homeViewMode: ScheduleViewModel? = null
    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var scheduleDoneAdapter: ScheduleDoneAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        bottomNav_home.selectedItemId = R.id.nav_home_menu
        bottomNav_home.setOnNavigationItemSelectedListener(navigationItemSelectedListener())

        homeViewMode = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)
        homeViewMode?.getAllData()?.observe(this,Observer<List<ScheduleEntity>>{
            this.iniRecyviewScheduleDone(it)
        })
        homeViewMode?.getRowsSchedule()?.observe(this,Observer<List<ScheduleEntity>>{
            this.initRecyclerView(it)
            this.startService(it)
        })

        btn_add_home.setOnClickListener {
            startActivity(Intent(this,AddScheduleActivity::class.java))
        }

        btn_deleteAll.setOnClickListener {
            homeViewMode?.deleteAllData()
        }
    }

    private fun startService(scheduleEntity: List<ScheduleEntity>){
        if(scheduleEntity.isNotEmpty()){
            val serviceIntent = Intent(this,ScheduleService::class.java)
            startService(serviceIntent)
        }
        else{
            val alarmMngr = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val alarmIntent = Intent(this, AlarmReceiver::class.java)
            val anjing = PendingIntent.getBroadcast(this,1,alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmMngr?.cancel(anjing)

            val serviceIntent = Intent(this,ScheduleService::class.java)
            stopService(serviceIntent)
        }
    }

    private fun initRecyclerView(scheduleEntity: List<ScheduleEntity>){
        scheduleAdapter = ScheduleAdapter()
        scheduleAdapter.scheduleAdapter(scheduleEntity,this)
        rv_schedule_home.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity,LinearLayoutManager.HORIZONTAL,false)
            this.adapter = scheduleAdapter
        }
    }
    private fun iniRecyviewScheduleDone(scheduleEntity: List<ScheduleEntity>){
        scheduleDoneAdapter = ScheduleDoneAdapter()
        scheduleDoneAdapter.scheduleDoneAdapter(scheduleEntity,this)
        rv_scheduleRecent_home.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity,LinearLayoutManager.HORIZONTAL,false)
            this.adapter = scheduleDoneAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        bottomNav_home.selectedItemId = R.id.nav_home_menu
    }

    private fun navigationItemSelectedListener() =
        object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.nav_group_menu -> {
                        startActivity(
                            Intent(this@HomeActivity, GroupActivity::class.java)
                        )
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
                    }
                    R.id.nav_setting_menu -> {
                        startActivity(
                            Intent(this@HomeActivity, SettingActivity::class.java)
                        )
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
                    }
                }
                return true
            }
        }
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_down)
    }

    private fun popupDialog(scID: String){
        val dialogBuilder = AlertDialog.Builder(this,R.style.DialogTheme)

        dialogBuilder.apply {
            this.setTitle("Delete Schedule")
            this.setMessage("Are you sure you want to delete this schedule?")
            this.setPositiveButton("Delete",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    homeViewMode?.deleteByID(scID)
            })
            this.setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.cancel()
                })
            this.create().show()
        }
    }

    override fun itemClickListener(scheduleEntity: ScheduleEntity) {
        val intent = Intent(this, AddScheduleActivity::class.java)
        intent.putExtra("SC_ID",scheduleEntity.scheduleID)
        intent.putExtra("SC_TITLE",scheduleEntity.title)
        intent.putExtra("SC_DESC",scheduleEntity.description)
        intent.putExtra("SC_TIME",scheduleEntity.timestamp)
        intent.putExtra("SC_BGCOLOR",scheduleEntity.bgcolor)
        intent.putExtra("SC_WITHTIME",scheduleEntity.with_time)
        intent.putExtra("SC_REMINDME",scheduleEntity.remindMe)

        startActivity(intent)
    }

    override fun itemLongClickListener(scheduleEntity: ScheduleEntity) {
        val scID = scheduleEntity.scheduleID
        popupDialog(scID)
    }
}