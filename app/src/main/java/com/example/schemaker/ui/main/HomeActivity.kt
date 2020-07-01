package com.example.schemaker.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schemaker.R
import com.example.schemaker.ScheduleService
import com.example.schemaker.adapter.ScheduleAdapter
import com.example.schemaker.model.ScheduleEntity
import com.example.schemaker.ui.AddScheduleActivity
import com.example.schemaker.viewmodel.ScheduleViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), ScheduleAdapter.ItemClickListener {

    private var homeViewMode: ScheduleViewModel? = null
    private lateinit var scheduleAdapter: ScheduleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        bottomNav_home.selectedItemId = R.id.nav_home_menu
        bottomNav_home.setOnNavigationItemSelectedListener(navigationItemSelectedListener())

        homeViewMode = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)
        homeViewMode?.getAllData()?.observe(this,Observer<List<ScheduleEntity>>{ this.initRecyclerView(it)})
        homeViewMode?.getRowsSchedule()?.observe(this,Observer<List<ScheduleEntity>>{this.startService(it)})

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
            serviceIntent.putExtra("SC_ID",scheduleEntity.get(0).scheduleID)
            serviceIntent.putExtra("SC_TIME",scheduleEntity.get(0).timestamp)
            serviceIntent.putExtra("SC_TITLE",scheduleEntity.get(0).title)
            startService(serviceIntent)
        }
        else{
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
}