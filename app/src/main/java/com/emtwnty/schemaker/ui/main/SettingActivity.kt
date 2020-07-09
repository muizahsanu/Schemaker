package com.emtwnty.schemaker.ui.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProviders
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.viewmodel.LoginViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    companion object{
        private const val RC_SIGN_IN = 9001
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mLoginViewModel: LoginViewModel
    private lateinit var mSharedPrefSetting: SharedPreferences
    private var ID_PREF_SETTING = "com.emtwnty.schemaker-setting"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        /** INIT Login View Model **/
        mLoginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        /** Membuat Google Signin Options **/
        mAuth = FirebaseAuth.getInstance()

        mSharedPrefSetting = getSharedPreferences(ID_PREF_SETTING,Context.MODE_PRIVATE)

        /** INIT bottom navigation klik listener **/
        bottomNav_setting.selectedItemId = R.id.nav_setting_menu
        bottomNav_setting.setOnNavigationItemSelectedListener(navigationItemSelectedListener())


        val lighMode = mSharedPrefSetting.getBoolean("LIGHMODE",false)
        btn_changeTheme.isChecked = lighMode
        btn_changeTheme.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                val editor = mSharedPrefSetting.edit()
                when(p1){
                    true->{
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        editor.putBoolean("LIGHMODE",true)
                    }
                    false->{
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        editor.putBoolean("LIGHMODE",false)
                    }
                }
                editor.apply()
                editor.commit()
            }
        })

        btn_logout_setting.setOnClickListener {
            lin_progressbar_setting.visibility = View.VISIBLE
            mLoginViewModel.signOutUser()
            updateUserUI()
        }
        btn_signin_setting.setOnClickListener{
            lin_progressbar_setting.visibility = View.VISIBLE
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
        lin_progressbar_setting.visibility = View.INVISIBLE
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