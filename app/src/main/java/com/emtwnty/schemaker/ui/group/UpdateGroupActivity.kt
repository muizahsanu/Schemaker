package com.emtwnty.schemaker.ui.group

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.ui.main.GroupActivity
import com.emtwnty.schemaker.viewmodel.GroupViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_group.*
import kotlinx.android.synthetic.main.activity_update_group.*

class UpdateGroupActivity : AppCompatActivity() {

    private lateinit var mGroupViewModel: GroupViewModel

    private lateinit var exGroupID: String
    private lateinit var exGroupName: String
    private lateinit var exGroupDesc: String
    private lateinit var exGroupImage: String

    private var newGroupImage: Uri? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_group)

        mGroupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)

        exGroupID = intent.getStringExtra("GROUP_ID").toString()
        exGroupName = intent.getStringExtra("GROUP_NAME").toString()
        exGroupDesc = intent.getStringExtra("GROUP_DESC").toString()
        exGroupImage = intent.getStringExtra("GROUP_IMAGE").toString()
        updateUI()

        rl_selectImage_updateGroup.setOnClickListener {
            val selectImageIntent = Intent(Intent.ACTION_PICK)
            selectImageIntent.setType("image/*")
            startActivityForResult(selectImageIntent,55)
        }

        btn_update_updateGroup.setOnClickListener {
            nullValidation()
        }
        btn_cancel_updateGroup.setOnClickListener {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 55 && data !=null && data.data != null){
            val imageFromDevice = data.data
            Picasso.get().load(imageFromDevice).into(iv_groupImage_updateGroup)
            newGroupImage = imageFromDevice
        }
    }

    private fun nullValidation(){
        val et_groupName = et_groupName_updateGroup.text
        val et_groupDesc = et_groupDesc_updateGroup.text
        if(et_groupName.isNullOrEmpty() || et_groupName.isNullOrBlank()){
            etLayout_groupName_updateGroup.isErrorEnabled = true
            etLayout_groupName_updateGroup.error = "Group name cannot be empty"
            return
        }
        etLayout_groupName_updateGroup.isErrorEnabled = false

        if(et_groupDesc.isNullOrEmpty() || et_groupDesc.isNullOrBlank()){
            etLayout_groupDesc_updateGroup.isErrorEnabled = true
            etLayout_groupDesc_updateGroup.error = "Group descriptions cannot be empty"
            return
        }
        etLayout_groupDesc_updateGroup.isErrorEnabled = false

        similarityValidation(et_groupName.toString(), et_groupDesc.toString())

    }
    private fun similarityValidation(newGroupName: String, newGroupDesc: String){
        if(newGroupName == exGroupName && newGroupDesc == exGroupDesc && newGroupImage.toString() == "null") {
            super.onBackPressed()
        }
        else{
            val newDataGroup = HashMap<String,Any>()
            newDataGroup.put("groupName",newGroupName)
            newDataGroup.put("groupDesc",newGroupDesc)
            if(newGroupImage.toString() == "null"){
                newDataGroup.put("groupImage",exGroupImage)
                mGroupViewModel.updateGroup(exGroupID,newDataGroup)
            }
            else{
                mGroupViewModel.uploadImage(newGroupImage!!,exGroupID).observe(this,
                    Observer {
                        newDataGroup.put("groupImage",it)
                        mGroupViewModel.result().observe(this,Observer<String>{
                            if(it == "FINISH_UPLOAD_IMAGE"){
                                mGroupViewModel.updateGroup(exGroupID,newDataGroup)
                            }
                        })
                    })
            }
        }
        getResult()
    }
    private fun getResult(){
        mGroupViewModel.result().observe(this,Observer<String>{
            when(it){
                "FINISH"-> {
                    Toast.makeText(this,"Group Updated", Toast.LENGTH_SHORT).show()
                    super.onBackPressed()
                }
                "UPDATING"->{
                    if(lin_progressbar_updateGroup.visibility == View.GONE){
                        btn_update_updateGroup.isEnabled = false
                        lin_progressbar_updateGroup.visibility = View.VISIBLE
                    }
                }
                "UPLOAD_IMAGE"->{
                    if(lin_progressbar_updateGroup.visibility == View.GONE){
                        btn_update_updateGroup.isEnabled = false
                        lin_progressbar_updateGroup.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun updateUI(){
        et_groupName_updateGroup.setText(exGroupName)
        et_groupDesc_updateGroup.setText(exGroupDesc)
        if(exGroupImage != "null"){
            Picasso.get().load(exGroupImage).into(iv_groupImage_updateGroup)
        }
    }
}