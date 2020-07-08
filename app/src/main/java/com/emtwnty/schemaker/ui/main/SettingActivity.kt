package com.emtwnty.schemaker.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProviders
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.viewmodel.LoginViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    companion object{
        private const val RC_SIGN_IN = 9001
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mLoginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        /** INIT Login View Model **/
        mLoginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        /** Membuat Google Signin Options **/
        mAuth = FirebaseAuth.getInstance()

        /** INIT bottom navigation klik listener **/
        bottomNav_setting.selectedItemId = R.id.nav_setting_menu
        bottomNav_setting.setOnNavigationItemSelectedListener(navigationItemSelectedListener())

        btn_changeTheme.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        btn_logout_setting.setOnClickListener {
            mLoginViewModel.signOutUser()
            updateUserUI()
        }
        btn_signin_setting.setOnClickListener{
            startActivityForResult(mLoginViewModel.signInIntent, RC_SIGN_IN)
        }


        updateUserUI()
    }

    override fun onResume() {
        super.onResume()
        bottomNav_setting.selectedItemId = R.id.nav_setting_menu
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            mLoginViewModel.signInGoogle(data)
            updateUserUI()
        }
    }

    private fun updateUserUI(){
        val currentUser = mAuth.currentUser
        if(currentUser != null){
            btn_logout_setting.visibility = View.VISIBLE
            btn_signin_setting.visibility = View.GONE
        }
        else{
            btn_logout_setting.visibility = View.GONE
            btn_signin_setting.visibility = View.VISIBLE
        }
    }

    private fun navigationItemSelectedListener()=
        object : BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.itemId){
                    R.id.nav_home_menu ->{
                        val intent = Intent(this@SettingActivity,HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
                    }
                    R.id.nav_group_menu ->{
                        startActivity(Intent(this@SettingActivity,GroupActivity::class.java))
                        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
                    }
                }
                return true
            }
        }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_down)
    }
}