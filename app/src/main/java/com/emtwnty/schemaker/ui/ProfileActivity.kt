package com.emtwnty.schemaker.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.model.online.UsersModel
import com.emtwnty.schemaker.viewmodel.UsersViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var userIDFromExtra: String
    private lateinit var mUsersViewModel: UsersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        userIDFromExtra = intent.getStringExtra("USER_ID").toString()
        mUsersViewModel = ViewModelProviders.of(this).get(UsersViewModel::class.java)

        mUsersViewModel.getUserDataByID(userIDFromExtra).observe(this,
            Observer<UsersModel>{dataUser->
                tv_username_profile.text = dataUser.username
                tv_userFullName_profile.text = dataUser.fullname
                Picasso.get().load(dataUser.imageURI).into(iv_userImage_profile)
            })
    }
}