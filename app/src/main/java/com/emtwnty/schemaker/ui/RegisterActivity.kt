package com.emtwnty.schemaker.ui

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Pair
import android.util.Patterns
import android.view.View
import com.emtwnty.schemaker.R
import kotlinx.android.synthetic.main.activity_register.*
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btn_register_register.setOnClickListener {
            validationForm()
        }
        btn_login_register.setOnClickListener {
            val options = ActivityOptions.makeSceneTransitionAnimation(this,
                Pair<View,String>(inputLayout_email_register,"email_trans"),
                Pair<View,String>(inputLayout_password_register,"password_trans")
            )
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent,options.toBundle())
        }
    }

    private fun validationForm(){
        val etEmail = et_email_register.text.toString()
        val etFullname = et_fullname_register.text.toString()
        val etUsername = et_username_register.text.toString()
        val etPassword = et_password_register.text.toString()
        val usernamePattern = Pattern.compile("^[a-z0-9_-]{3,15}$")

        if(etEmail.isEmpty()){
            inputLayout_email_register.error = "Email must not be empty"
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(etEmail).matches()){
            inputLayout_email_register.error = "Invalid email"
            return
        }
        inputLayout_email_register.isErrorEnabled = false

        if(etFullname.isEmpty()){
            inputLayout_fullname_register.error = "Fullname must not be empty"
            return
        }
        inputLayout_fullname_register.isErrorEnabled = false

        if(etUsername.isEmpty()){
            inputLayout_username_register.error = "Username must not be empty"
            return
        }
        if(!usernamePattern.matcher(etUsername).matches()){
            inputLayout_username_register.error = "Invalid username"
            return
        }
        inputLayout_username_register.isErrorEnabled = false

        if(etPassword.isEmpty()){
            inputLayout_password_register.error = "Password must not be empty"
            return
        }
        inputLayout_password_register.isErrorEnabled = false
    }

}