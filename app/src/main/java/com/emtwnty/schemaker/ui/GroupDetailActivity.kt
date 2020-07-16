package com.emtwnty.schemaker.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.model.online.GroupModel
import com.emtwnty.schemaker.viewmodel.GroupViewModel
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_group_detail.*

class GroupDetailActivity : AppCompatActivity() {

    private lateinit var mGroupViewModel: GroupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_detail)


        // init
        mGroupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)

        val groupID = intent.getStringExtra("GROUP_ID")
        if(groupID != null){
            mGroupViewModel.getGroupDataByID(groupID).observe(this, Observer {
                updateGroupUI(it)
            })
            val fragment = GroupSchduleFragment.newInstance(groupID)
            replaceFragment(fragment)
        }

        btn_search_profile.setOnClickListener {
            val result_search = et_search_profile.text.toString()
//            findSelectedFragment(result_search)
        }


        tabLayout_profile.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val groupID = intent.getStringExtra("GROUP_ID").toString()
                val fragment: Fragment
                when(tab?.position){
                    0-> {
                        fragment = GroupSchduleFragment.newInstance(groupID)
                    }
                    else-> {
                        fragment = MembersFragment.newInstance(groupID)
                    }
                }
                replaceFragment(fragment)
            }
        })
    }

    private fun updateGroupUI(groupModel: GroupModel){
        tv_titleGroup_detailGroup.text = groupModel.groupName
        if(groupModel.groupImage != "null"){
            Picasso.get().load(groupModel.groupImage).into(iv_groupImage_detailGroup)
        }
    }

    private fun findSelectedFragment(resultSearch:String){
        val groupID = intent.getStringExtra("GROUP_ID")
        val fragment: Fragment
        if(tabLayout_profile.selectedTabPosition == 0){
            fragment = GroupSchduleFragment.newInstance(resultSearch)
        } else fragment = MembersFragment.newInstance(groupID!!)
        replaceFragment(fragment)
    }

    private fun replaceFragment(fragment:Fragment){
        val fragTrans = supportFragmentManager.beginTransaction()
        fragTrans.replace(R.id.framelayout,fragment)
        fragTrans.commit()
    }
}