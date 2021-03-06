package com.emtwnty.schemaker.ui.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.ActivityOptions
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Pair
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.get
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
import com.emtwnty.schemaker.ui.ProfileActivity
import com.emtwnty.schemaker.viewmodel.GroupViewModel
import com.emtwnty.schemaker.viewmodel.ScheduleViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*
import kotlin.concurrent.schedule

class HomeActivity : AppCompatActivity(), ScheduleAdapter.ItemClickListener,ScheduleDoneAdapter.ItemClickListener {

    private var homeViewMode: ScheduleViewModel? = null
    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var scheduleDoneAdapter: ScheduleDoneAdapter
    private lateinit var mSharedPref: SharedPreferences
    private var ID_PRE = "com.emtwnty.schemaker-user"

    private lateinit var mGroupViewModel: GroupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        lin_progressbar_home.bringToFront()
        lin_progressbar_home.setOnClickListener{}

        bottomNav_home.selectedItemId = R.id.nav_home_menu
        bottomNav_home.setOnNavigationItemSelectedListener(navigationItemSelectedListener())

        mSharedPref = getSharedPreferences(ID_PRE,Context.MODE_PRIVATE)

        mGroupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)
        homeViewMode = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)
        /** [ Mengambil jadwal/task yang TIDAK akan diingatkan ] **/
        homeViewMode?.getDonetask()?.observe(this,Observer<List<ScheduleEntity>>{
            this.iniRecyviewScheduleDone(it)
        })
        /** [ Mengambil jadwal/task yang AKAN diingatkan ] **/
        homeViewMode?.getRemindTask()?.observe(this,Observer<List<ScheduleEntity>>{
        })
        /** [ Mengambil jadwal/task yang BELUM selesai ] **/
        homeViewMode?.getNotDoneTask()?.observe(this,Observer<List<ScheduleEntity>>{
            this.initRecyclerView(it)
            this.startService(it)
        })

        btn_add_home.setOnClickListener {
            startActivity(Intent(this,AddScheduleActivity::class.java))
        }

        btn_deleteAll_home.setOnClickListener {
            val halo = scheduleDoneAdapter.itemCount
            val animationDuration = resources.getInteger(android.R.integer.config_longAnimTime)
            if(halo != 0){
                lin_progressbar_home.visibility = View.VISIBLE
                for (i in 0..halo-1){
                    Timer().schedule(1000){
                        val asd = rv_scheduleRecent_home.get(i)
                        asd.animate()
                            .alpha(0f)
                            .setDuration(animationDuration.toLong())
                            .setListener(object : AnimatorListenerAdapter(){
                                override fun onAnimationEnd(animation: Animator?) {
                                    homeViewMode?.deleteDoneTask()
                                    lin_progressbar_home.visibility = View.GONE
                                }
                            })
                    }
                }
            }
        }

        iv_userImage_home.setOnClickListener {
            val userID = mSharedPref.getString("USER_ID",null).toString()
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("USER_ID",userID)
            startActivity(intent)
        }

        // Check apakah dia masuk dengan link invitation atau tidak
        checkInvitationLink()
    }

    /** [ Start ] Check Invitation **/
    private fun checkInvitationLink(){
        val groupIDFromLink = intent.getStringExtra("GROUPID_FROMLINK")
        if(groupIDFromLink != null){
            showDialogInvitation(groupIDFromLink)
        }
    }

    /** [ Start ] Show dialog invitation **/
    private fun showDialogInvitation(groupIDFromLink: String){
        val alertDialog = AlertDialog.Builder(this,R.style.DialogTheme)
        alertDialog
            .setTitle("Invitation")
            .setMessage("Join the group?")
            .setPositiveButton("Join",
            DialogInterface.OnClickListener { dialogInterface, i ->
                // Join The group
                mGroupViewModel.addMemberGroup(groupIDFromLink)
            })
            .setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialogInterface, i ->
                //  Cancel dan tidak join group
                dialogInterface.cancel()
            })
        alertDialog.create().show()
    }

    /** [Menjalankan service jika ada jadwal/task yang akan diingatkan] **/
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

    /** [Menampilkan data task yang belum selesai ke dalam recycler view] **/
    private fun initRecyclerView(scheduleEntity: List<ScheduleEntity>){
        if(scheduleEntity.isEmpty()){
            rv_schedule_home.visibility = View.GONE
            tv_emptyTask_home.visibility = View.VISIBLE
        }
        else{
            rv_schedule_home.visibility = View.VISIBLE
            tv_emptyTask_home.visibility = View.GONE
        }

        scheduleAdapter = ScheduleAdapter()
        rv_schedule_home.apply {
            scheduleAdapter.scheduleAdapter(scheduleEntity,this@HomeActivity)
            scheduleAdapter.notifyDataSetChanged()
            layoutManager = LinearLayoutManager(this@HomeActivity)
            this.adapter = scheduleAdapter
            this.adapter?.notifyDataSetChanged()
        }
    }

    /** [Menampilkan data task yang sudah selesai ke dalam recycler view] **/
    private fun iniRecyviewScheduleDone(scheduleEntity: List<ScheduleEntity>){
        if(scheduleEntity.isEmpty()){
            rv_scheduleRecent_home.visibility = View.GONE
            tv_completedTask_home.visibility = View.VISIBLE
        }
        else{
            rv_scheduleRecent_home.visibility = View.VISIBLE
            tv_completedTask_home.visibility = View.GONE
        }

        scheduleDoneAdapter = ScheduleDoneAdapter()
        rv_scheduleRecent_home.apply {
            scheduleDoneAdapter.scheduleDoneAdapter(scheduleEntity,this@HomeActivity)
            layoutManager = LinearLayoutManager(this@HomeActivity)
            this.adapter = scheduleDoneAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        bottomNav_home.selectedItemId = R.id.nav_home_menu
    }

    /** [ START-- ] Navigation Item Selected. ketika user klik menu pada bottom nav**/
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

    override fun onStart() {
        super.onStart()
        getDataUserPref()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_down)
    }

    /** [ START-- ] Dialog delete item **/
    private fun popupDialog(scID: String, position: Int,v: View){
        val dialogBuilder = AlertDialog.Builder(this,R.style.DialogTheme)

        dialogBuilder.apply {
            this.setTitle("Delete Schedule")
            this.setMessage("Are you sure you want to delete this schedule?")
            this.setPositiveButton("Delete",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    val animationDuration = resources.getInteger(android.R.integer.config_longAnimTime)
                    v.animate()
                        .alpha(0f)
                        .setDuration(animationDuration.toLong())
                        .setListener(object : AnimatorListenerAdapter(){
                            override fun onAnimationEnd(animation: Animator?) {
                                homeViewMode?.deleteByID(scID)
                            }
                        })
            })
            this.setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.cancel()
                })
            this.create().show()
        }
    }
    /** [ END-- ] Dialog delete item **/


    private fun getDataUserPref(){
        val userName = mSharedPref.getString("USER_NAME",null)
        val userImage = mSharedPref.getString("USER_IMAGE",null)
        if(userName !=null && userImage != null){
            iv_userImage_home.visibility = View.VISIBLE
            updateUI(userName,userImage)
        }else iv_userImage_home.visibility = View.GONE
    }

    private fun updateUI(userName:String,userImage:String){
        tv_userName_home.text = userName
        Picasso.get().load(userImage).into(iv_userImage_home)

    }


    /** [Ketika user klik salah satu item di Recycler View] **/
    override fun itemClickListener(scheduleEntity: ScheduleEntity, position: Int) {
        val intent = Intent(this, AddScheduleActivity::class.java)
        intent.putExtra("SC_ID",scheduleEntity.scheduleID)
        intent.putExtra("SC_TITLE",scheduleEntity.title)
        intent.putExtra("SC_DESC",scheduleEntity.description)
        intent.putExtra("SC_TIME",scheduleEntity.timestamp)
        intent.putExtra("SC_BGCOLOR",scheduleEntity.bgcolor)
        intent.putExtra("SC_WITHTIME",scheduleEntity.with_time)
        intent.putExtra("SC_REMINDME",scheduleEntity.remindMe)

        val cv = rv_schedule_home.get(position).findViewById<CardView>(R.id.cv_schedule_layoutRV)
        val lin = rv_schedule_home.get(position).findViewById<LinearLayout>(R.id.lin_layourRV)

        val options = ActivityOptions.makeSceneTransitionAnimation(this,
            Pair<View,String>(cv,"trans_cv_to_addSche"),
            Pair<View,String>(lin,"trans_lin_to_toolbar")
        )
        startActivity(intent,options.toBundle())
    }

    override fun itemClickListenerDone(scheduleEntity: ScheduleEntity, position: Int) {
        val intent = Intent(this, AddScheduleActivity::class.java)
        intent.putExtra("SC_ID",scheduleEntity.scheduleID)
        intent.putExtra("SC_TITLE",scheduleEntity.title)
        intent.putExtra("SC_DESC",scheduleEntity.description)
        intent.putExtra("SC_TIME",scheduleEntity.timestamp)
        intent.putExtra("SC_BGCOLOR",scheduleEntity.bgcolor)
        intent.putExtra("SC_WITHTIME",scheduleEntity.with_time)
        intent.putExtra("SC_REMINDME",scheduleEntity.remindMe)

        val cv = rv_scheduleRecent_home.get(position).findViewById<CardView>(R.id.cv_schedule_layoutRV)
        val lin = rv_scheduleRecent_home.get(position).findViewById<LinearLayout>(R.id.lin_layourRV)

        val options = ActivityOptions.makeSceneTransitionAnimation(this,
            Pair<View,String>(cv,"trans_cv_to_addSche"),
            Pair<View,String>(lin,"trans_lin_to_toolbar")
        )
        startActivity(intent,options.toBundle())
    }

    /** [ Ketika user menekan sedikit lama salah satu item di Recycler View ] **/
    override fun itemLongClickListener(scheduleEntity: ScheduleEntity,position:Int) {
        val scID = scheduleEntity.scheduleID
        val v = rv_schedule_home.get(position)
        popupDialog(scID,position,v)
    }

    override fun itemLongClickListenerDone(scheduleEntity: ScheduleEntity, position: Int) {
        val scID = scheduleEntity.scheduleID
        val v = rv_scheduleRecent_home.get(position)
        popupDialog(scID,position,v)
    }
}