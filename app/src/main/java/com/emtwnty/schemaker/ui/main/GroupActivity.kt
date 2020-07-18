package com.emtwnty.schemaker.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.adapter.GroupScheAdapter
import com.emtwnty.schemaker.adapter.GroupsAdapter
import com.emtwnty.schemaker.model.online.GroupModel
import com.emtwnty.schemaker.model.online.ScheduleOnlineModel
import com.emtwnty.schemaker.ui.AddGroupActivity
import com.emtwnty.schemaker.ui.GroupDetailActivity
import com.emtwnty.schemaker.viewmodel.GroupViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_group.*

class GroupActivity : AppCompatActivity(), GroupsAdapter.onItemClickListener {

    private lateinit var mGroupViewModel: GroupViewModel
    private lateinit var mGroupsAdapter: GroupsAdapter
    private lateinit var mGroupScheAdapter: GroupScheAdapter
    private lateinit var mAuth: FirebaseAuth

    private lateinit var groupID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        mGroupsAdapter = GroupsAdapter()
        mGroupScheAdapter = GroupScheAdapter()
        mAuth = FirebaseAuth.getInstance()

        bottomNav_group.selectedItemId = R.id.nav_group_menu
        bottomNav_group.setOnNavigationItemSelectedListener(navigationItemSelectedListener())

        btn_addGroup_group.setOnClickListener {
            startActivity(Intent(this,AddGroupActivity::class.java))
        }

        btn_viewgroup_group.setOnClickListener {
            val intent = Intent(this, GroupDetailActivity::class.java)
            intent.putExtra("GROUP_ID",groupID)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        bottomNav_group.selectedItemId = R.id.nav_group_menu
    }

    override fun onStart() {
        super.onStart()
        if(mAuth.currentUser != null){
            konten_group.visibility = View.VISIBLE
            lin_notLogin_group.visibility = View.GONE

            mGroupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)
            retriveDataGroup()
        }
        else{
            konten_group.visibility = View.GONE
            lin_notLogin_group.visibility = View.VISIBLE
        }
    }

    private fun retriveDataGroup(){
        mGroupViewModel.getAllGroup().observe(this, Observer<List<GroupModel>>{
            if(it != null){
                mGroupsAdapter.groupsAdapter(it,this)
                rv_listGroup_group.apply {
                    layoutManager = LinearLayoutManager(this@GroupActivity,LinearLayoutManager.HORIZONTAL,false)
                    adapter = mGroupsAdapter
                }
            } else mGroupViewModel.iniGetGroupData()
        })
    }

    private fun navigationItemSelectedListener() =
        object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.nav_home_menu -> {
                        val intent = Intent(this@GroupActivity,HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
                    }
                    R.id.nav_setting_menu -> {
                        startActivity(
                            Intent(this@GroupActivity, SettingActivity::class.java)
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

    private fun setDataScheduleToRecyclerView(scheduleList: List<ScheduleOnlineModel>){
        rv_scheduleGroup_group.apply {
            layoutManager = LinearLayoutManager(this@GroupActivity)
            mGroupScheAdapter = GroupScheAdapter()
            mGroupScheAdapter.groupScheAdapter(scheduleList)
            adapter = mGroupScheAdapter
        }
    }

    override fun itemClickListener(groupModel: GroupModel, position: Int) {
        val intent = Intent(this, GroupDetailActivity::class.java)
        intent.putExtra("GROUP_ID",groupModel.groupID)
        startActivity(intent)
    }
}