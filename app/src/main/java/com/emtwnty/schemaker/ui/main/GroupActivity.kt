package com.emtwnty.schemaker.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.adapter.GroupScheAdapter
import com.emtwnty.schemaker.adapter.GroupsAdapter
import com.emtwnty.schemaker.model.online.GroupModel
import com.emtwnty.schemaker.ui.group.AddGroupActivity
import com.emtwnty.schemaker.ui.group.GroupDetailActivity
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
            startActivity(Intent(this,
                AddGroupActivity::class.java))
        }

        searchView_group.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(p0: String?): Boolean {
                mGroupsAdapter.filter.filter(p0)
                return false
            }
        })
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
            btn_addGroup_group.visibility = View.VISIBLE

            mGroupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)
            retriveDataGroup()
        }
        else{
            konten_group.visibility = View.GONE
            lin_notLogin_group.visibility = View.VISIBLE
            btn_addGroup_group.visibility = View.GONE
        }
    }

    private fun retriveDataGroup(){
        mGroupViewModel.getAllGroup().observe(this, Observer<List<GroupModel>>{
            if(it.toString() != "[]"){
                val asd = it.toString()
                println("Group_data => ${asd}")
                rv_listGroup_group.visibility = View.VISIBLE
                tv_noGroup_group.visibility = View.GONE

                mGroupsAdapter.groupsAdapter(it,this)
                rv_listGroup_group.apply {
                    layoutManager = LinearLayoutManager(this@GroupActivity)
                    adapter = mGroupsAdapter
                }

            } else {
                rv_listGroup_group.visibility = View.GONE
                tv_noGroup_group.visibility = View.VISIBLE
//                mGroupViewModel.iniGetGroupData()
            }
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

    override fun itemClickListener(groupModel: GroupModel, position: Int) {
        val userID = mAuth.currentUser?.uid
        val halo = groupModel.role
        val roleCurentUser = halo[userID].toString()

        val intent = Intent(this, GroupDetailActivity::class.java)
        intent.putExtra("GROUP_ID",groupModel.groupID)
        intent.putExtra("GROUP_NAME",groupModel.groupName)
        intent.putExtra("GROUP_DESC",groupModel.groupDesc)
        intent.putExtra("GROUP_IMAGE",groupModel.groupImage)
        intent.putExtra("CURRENT_USER_ROLE",roleCurentUser)
        startActivity(intent)
    }
}