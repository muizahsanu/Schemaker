package com.emtwnty.schemaker.ui.group

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.ui.GroupSchduleFragment
import com.emtwnty.schemaker.ui.MembersFragment
import com.emtwnty.schemaker.ui.main.HomeActivity
import com.emtwnty.schemaker.viewmodel.GroupScheViewModel
import com.emtwnty.schemaker.viewmodel.GroupViewModel
import com.emtwnty.schemaker.viewmodel.MembersViewModel
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_group_detail.*

class GroupDetailActivity : AppCompatActivity() {

    private lateinit var mGroupViewModel: GroupViewModel
    private lateinit var mGroupScheViewModel: GroupScheViewModel
    private lateinit var mMembersViewModel: MembersViewModel
    private lateinit var mMenu: Menu

    private lateinit var groupID: String
    private lateinit var groupName:String
    private lateinit var groupDesc: String
    private lateinit var groupImage: String
    private lateinit var currentUserRole: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_detail)

        groupID = intent.getStringExtra("GROUP_ID").toString()
        groupName = intent.getStringExtra("GROUP_NAME").toString()
        groupDesc = intent.getStringExtra("GROUP_DESC").toString()
        groupImage = intent.getStringExtra("GROUP_IMAGE").toString()
        currentUserRole = intent.getStringExtra("CURRENT_USER_ROLE").toString()

        toolbar_groupDetail.setOnMenuItemClickListener(toolbarItemClickListener())
        mMenu = toolbar_groupDetail.menu

        // init
        mGroupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)
        mGroupScheViewModel = ViewModelProviders.of(this).get(GroupScheViewModel::class.java)
        mMembersViewModel = ViewModelProviders.of(this).get(MembersViewModel::class.java)

        mMembersViewModel.initGetUserData(groupID)
        mGroupScheViewModel.initGetGroupSche(groupID)

        checkUserRole()
        updateGroupUI()

        val fragment = GroupSchduleFragment.newInstance(groupID,currentUserRole)
        replaceFragment(fragment)

        btn_search_profile.setOnClickListener {
            val result_search = et_search_profile.text.toString()
//            findSelectedFragment(result_search)
        }


        // Tab Layout Click Listener
        tabLayout_profile.addOnTabSelectedListener(tabLayoutSelectedListener(groupID))
    }

    private fun tabLayoutSelectedListener(groupID: String) =
        object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val fragment: Fragment
                when (tab?.position) {
                    0 -> fragment = GroupSchduleFragment.newInstance(groupID,currentUserRole)
                    else -> fragment = MembersFragment.newInstance(groupID,currentUserRole)
                }
                replaceFragment(fragment)
            }
        }

    private fun checkUserRole(){
        if(currentUserRole == "hokage"){
            mMenu.findItem(R.id.deletGroup_menu).isVisible = true
            mMenu.findItem(R.id.editGroup_menu).isVisible = true
            mMenu.findItem(R.id.leaveGroup_menu).isVisible = false
        }else{
            mMenu.findItem(R.id.deletGroup_menu).isVisible = false
            mMenu.findItem(R.id.editGroup_menu).isVisible = false
            mMenu.findItem(R.id.leaveGroup_menu).isVisible = true
        }
    }

    private fun toolbarItemClickListener() =
        Toolbar.OnMenuItemClickListener { item ->
            var dialogTitle = ""
            var dialogMessage = ""
            var dialogKey = ""
            when(item?.itemId){
                R.id.deletGroup_menu->{
                    dialogTitle = "Delete Group"
                    dialogMessage = "Are you sure you want to delete this group ?!"
                    dialogKey = "DELETE_GROUP"
                }
                R.id.leaveGroup_menu->{
                    dialogTitle = "Leave Group"
                    dialogMessage = "Are you sure you want to leave this group?"
                    dialogKey = "LEAVE_GROUP"
                }
            }
            if(dialogKey.isNotEmpty()){
                showDialogDelete(dialogTitle,dialogMessage,dialogKey)
            }
            true
        }

    private fun showDialogDelete(dialogTitle: String,dialogMessage: String, dialogKey: String){
        val dialogBuilder = AlertDialog.Builder(this,R.style.DialogTheme)
        dialogBuilder
            .setTitle(dialogTitle)
            .setMessage(dialogMessage)
            .setPositiveButton("Delete",object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    if(dialogKey == "DELETE_GROUP"){
                        mGroupViewModel.deleteGroupByID(groupID)
                    }
                    else if(dialogKey == "LEAVE_GROUP"){
                        mMembersViewModel.leaveGroup(groupID)
                        Toast.makeText(this@GroupDetailActivity,"You left the group",Toast.LENGTH_SHORT).show()
                    }
                    val intent = Intent(this@GroupDetailActivity, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            })
            .setNegativeButton("Cancel",object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    p0?.cancel()
                }
            })
            .create().show()
    }

    private fun updateGroupUI(){
        tv_titleGroup_detailGroup.text = groupName
        tv_groupDesc_detailGroup.text = groupDesc
        if(groupImage != "null"){
            Picasso.get().load(groupImage).into(iv_groupImage_detailGroup)
        }
    }

    private fun findSelectedFragment(resultSearch: String) {
        val fragment: Fragment
        if (tabLayout_profile.selectedTabPosition == 0) {
            fragment = GroupSchduleFragment.newInstance(resultSearch,currentUserRole)
        } else fragment = MembersFragment.newInstance(groupID,currentUserRole)
        replaceFragment(fragment)
    }

    private fun replaceFragment(fragment:Fragment){
        val fragTrans = supportFragmentManager.beginTransaction()
        fragTrans.replace(R.id.framelayout,fragment)
        fragTrans.commit()
    }
}