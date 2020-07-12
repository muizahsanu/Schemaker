package com.emtwnty.schemaker.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.viewmodel.MembersViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_find_user.*

class FindUserActivity : AppCompatActivity() {

    private lateinit var mMembersViewModel: MembersViewModel
    private lateinit var mAuth: FirebaseAuth
    private var otherUserID: String = ""
    private var groupID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_user)

        mMembersViewModel = ViewModelProviders.of(this).get(MembersViewModel::class.java)
        mAuth = FirebaseAuth.getInstance()

        groupID = intent.getStringExtra("GROUP_ID").toString()

        btn_search_findUser.setOnClickListener {
            searchValidation()
        }
        btn_invite_findUser.setOnClickListener {
            if(otherUserID.isNotEmpty()){
                val userID = mAuth.currentUser?.uid.toString()
                mMembersViewModel.sendRequestGroup(otherUserID,userID,groupID)
            }
        }
    }

    private fun searchValidation(){
        val et_search = et_search_findUser.text
        if(et_search.isNullOrEmpty()){
            etLayout_search_findUser.isErrorEnabled = true
            etLayout_search_findUser.error = "You haven't entered anything yet"
            return
        }
        etLayout_search_findUser.isErrorEnabled = false

        val usernameEditText = et_search.toString()
        resultSearch(usernameEditText)
    }

    private fun resultSearch(usernameEditText: String){
        mMembersViewModel.findUserByUsername(usernameEditText)
            .observe(this, Observer {
                if(it.uid.isNotEmpty()){
                    println("USER_ID => ${it.uid}")
                    cv_result_findeUser.visibility = View.VISIBLE
                    Picasso.get().load(it.imageURI).into(iv_userImage_findUser)
                    tv_userName_findUser.text = it.username

                    otherUserID = it.uid
                }
                else{
                    cv_result_findeUser.visibility = View.GONE
                }
            })
    }
}