package com.example.schemaker.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import com.example.schemaker.R
import kotlinx.android.synthetic.main.activity_register.*
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btn_register_register.setOnClickListener {
            validationForm()
        }
    }

    private fun validationForm(){
        val etEmail = et_email_register.text.toString()
        val etFullname = et_fullname_register.text.toString()
        val etUsername = et_username_register.text.toString()
        val etPassword = et_password_register.text.toString()
        val usernamePattern = Pattern.compile("^[a-z0-9_-]{3,15}$")


        if(etEmail.isEmpty()){
            inputLayout_email_register.error = "Email tidak boleh kosong"
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(etEmail).matches()){
            inputLayout_email_register.error = "Email tidak valid"
        }
        inputLayout_email_register.isErrorEnabled = false

        if(etFullname.isEmpty()){
            inputLayout_fullname_register.error = "Fullname tidak boleh kosong"
            return
        }
        inputLayout_fullname_register.isErrorEnabled = false

        if(etUsername.isEmpty()){
            inputLayout_username_register.error = "Username tidak boleh kosong"
            return
        }
        if(!usernamePattern.matcher(etUsername).matches()){
            inputLayout_username_register.error = "Tidak boleh ada spasi"
            return
        }
        inputLayout_username_register.isErrorEnabled = false

        if(etPassword.isEmpty()){
            inputLayout_password_register.error = "Password tidak boleh kosong"
            return
        }
        inputLayout_password_register.isErrorEnabled = false
    }


}