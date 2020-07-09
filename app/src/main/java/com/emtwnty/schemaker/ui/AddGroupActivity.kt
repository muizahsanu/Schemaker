package com.emtwnty.schemaker.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.model.online.GroupListener
import com.emtwnty.schemaker.model.online.GroupModel
import com.emtwnty.schemaker.ui.main.GroupActivity
import com.emtwnty.schemaker.viewmodel.GroupViewModel
import kotlinx.android.synthetic.main.activity_add_group.*
import java.util.*
import kotlin.collections.HashMap

class AddGroupActivity : AppCompatActivity() {

    private lateinit var mGroupViewModel: GroupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_group)

        setSupportActionBar(toolbar_addgroup_group)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mGroupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)

        btn_create_addGroup.setOnClickListener {
            formValidation()
        }
    }

    private fun formValidation(){
        val etGroupName = et_groupName_addGroup.text
        val etGroupDesc = et_groupDesc_addGroup.text
        if(etGroupName.isNullOrEmpty()){
            etLayout_groupName_addGroup.isErrorEnabled = true
            etLayout_groupName_addGroup.error = "You have not entered a group name"
            return
        }
        etLayout_groupName_addGroup.isErrorEnabled = false
        if(etGroupDesc.isNullOrEmpty()){
            etLayout_groupDesc_addGroup.isErrorEnabled = true
            etLayout_groupDesc_addGroup.error = "You have not entered a group name"
            return
        }
        etLayout_groupDesc_addGroup.isErrorEnabled = false

        val groupMap = HashMap<String,Any>()
        val groupID = UUID.randomUUID().toString()
        groupMap.put("groupID",groupID)
        groupMap.put("groupName",etGroupName.toString())
        groupMap.put("groupDesc",etGroupDesc.toString())
        groupMap.put("groupImage","-")
        mGroupViewModel.addGroup(groupMap)
        getResult()
    }

    private fun getResult(){
        mGroupViewModel.result().observe(this,Observer<String>{
            when(it){
                "FINISH"-> {
                    Toast.makeText(this,"Group created",Toast.LENGTH_SHORT).show()
                    lin_progressbar_addGroup.visibility = View.INVISIBLE
                    startActivity(Intent(this,GroupActivity::class.java))
                    finish()
                }
                "FAILED"-> {
                    btn_create_addGroup.isEnabled = false
                    lin_progressbar_addGroup.visibility = View.INVISIBLE
                    Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
                }
                "RUNNING"-> {
                    btn_create_addGroup.isEnabled = false
                    lin_progressbar_addGroup.visibility = View.VISIBLE
                }
            }
        })
    }
}