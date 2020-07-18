package com.emtwnty.schemaker.ui

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.model.online.GroupModel
import com.emtwnty.schemaker.ui.main.HomeActivity
import com.emtwnty.schemaker.viewmodel.GroupViewModel
import com.emtwnty.schemaker.viewmodel.MembersViewModel
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_group_detail.*

class GroupDetailActivity : AppCompatActivity() {

    private lateinit var mGroupViewModel: GroupViewModel
    private lateinit var mMembersViewModel: MembersViewModel
    private lateinit var mMenu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_detail)

        checkUserRole()

        toolbar_groupDetail.setOnMenuItemClickListener(toolbarItemClickListener())
        mMenu = toolbar_groupDetail.menu

        // init
        mGroupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)
        mMembersViewModel = ViewModelProviders.of(this).get(MembersViewModel::class.java)


        val groupID = intent.getStringExtra("GROUP_ID")
        if(groupID != null){
            mGroupViewModel.initGetScheduleGroup(groupID)
            mMembersViewModel.initGetUserData(groupID)
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
                val fragment: Fragment
                when(tab?.position){
                    0-> {
                        fragment = GroupSchduleFragment.newInstance(groupID.toString())
                    }
                    else-> {
                        fragment = MembersFragment.newInstance(groupID.toString())
                    }
                }
                replaceFragment(fragment)
            }
        })
    }

    private fun checkUserRole(){
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        val groupID = intent.getStringExtra("GROUP_ID").toString()
        FirebaseFirestore.getInstance().collection("users").document(userID!!)
            .collection("groups").document(groupID)
            .get().addOnSuccessListener {
                if(it!=null){
                    val iniData = it.data as HashMap<*,*>
                    val role = iniData.get("role")
                    if(role == "hokage"){
                        mMenu.findItem(R.id.deletGroup_menu).setVisible(true)
                        mMenu.findItem(R.id.editGroup_menu).setVisible(true)
                        mMenu.findItem(R.id.leaveGroup_menu).setVisible(false)
                    }else{
                        mMenu.findItem(R.id.deletGroup_menu).setVisible(false)
                        mMenu.findItem(R.id.editGroup_menu).setVisible(false)
                        mMenu.findItem(R.id.leaveGroup_menu).setVisible(true)
                    }
                }
            }
    }

    private fun toolbarItemClickListener() = object : Toolbar.OnMenuItemClickListener{
        override fun onMenuItemClick(item: MenuItem?): Boolean {
            var dialogTitle = ""
            var dialogMessage = ""
            var dialogKey = ""
            when(item?.itemId){
                R.id.deletGroup_menu->{
                    dialogTitle = "Delete Group"
                    dialogMessage = "Are you sure you want to delete this group ?!"
                    dialogKey = "DELETE_GRUP"
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
            return true
        }
    }

    private fun showDialogDelete(dialogTitle: String,dialogMessage: String, dialogKey: String){
        val dialogBuilder = AlertDialog.Builder(this,R.style.DialogTheme)
        dialogBuilder
            .setTitle(dialogTitle)
            .setMessage(dialogMessage)
            .setPositiveButton("Delete",object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    val groupID = intent.getStringExtra("GROUP_ID").toString()
                    if(dialogKey == "DELETE_GROUP"){
                        mGroupViewModel.deleteGroupByID(groupID)
                    }
                    else if(dialogKey == "LEAVE_GROUP"){
                        mMembersViewModel.leaveGroup(groupID)
                        Toast.makeText(this@GroupDetailActivity,"Keluar Group",Toast.LENGTH_SHORT).show()
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