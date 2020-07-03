package com.emtwnty.schemaker.ui

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.emtwnty.schemaker.R
import kotlinx.android.synthetic.main.activity_login.*
import android.util.Pair

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_register_login.setOnClickListener {
            val options = ActivityOptions.makeSceneTransitionAnimation(this,
                Pair<View,String>(inputLayout_email_login,"email_trans"),
                Pair<View,String>(inputLayout_password_login,"password_trans"))
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent,options.toBundle())
        }
    }
}