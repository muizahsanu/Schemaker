package com.example.schemaker.ui.main

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schemaker.R
import com.example.schemaker.adapter.ScheduleAdapter
import com.example.schemaker.model.ScheduleEntity
import com.example.schemaker.ui.AddScheduleActivity
import com.example.schemaker.viewmodel.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_addschedule_dialog.view.*
import java.util.*

class HomeActivity : AppCompatActivity() {

    private var homeViewMode: HomeViewModel? = null
    private lateinit var scheduleAdapter: ScheduleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        bottomNav_home.selectedItemId = R.id.nav_home_menu
        bottomNav_home.setOnNavigationItemSelectedListener(navigationItemSelectedListener())

        homeViewMode = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        homeViewMode?.getAllData()?.observe(this,Observer<List<ScheduleEntity>>{this.initRecyclerView(it)})

        btn_add_home.setOnClickListener {
            startActivity(Intent(this,AddScheduleActivity::class.java))
        }
    }

    private fun initRecyclerView(scheduleEntity: List<ScheduleEntity>){
        scheduleAdapter = ScheduleAdapter()
        scheduleAdapter.scheduleAdapter(scheduleEntity)
        rv_schedule_home.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
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
}