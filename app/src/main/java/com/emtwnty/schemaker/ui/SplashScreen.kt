package com.emtwnty.schemaker.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Pair
import android.view.View
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.ui.main.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreen : AppCompatActivity() {

    private var shortAnimationDuration: Long = 1500
    private lateinit var mAuth: FirebaseAuth

    private var ID_PREF = "com.emtwnty.schemaker-user"
    private lateinit var mSharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        mAuth = FirebaseAuth.getInstance()
        mSharedPref = getSharedPreferences(ID_PREF, Context.MODE_PRIVATE)


        tv_appVersion_splash.animate()
            .alpha(1f)
            .setDuration(shortAnimationDuration)
            .setListener(object : AnimatorListenerAdapter(){
                override fun onAnimationStart(animation: Animator?) {
                    tv_slogan_splash.animate()
                        .alpha(1f)
                        .setDuration(shortAnimationDuration)
                        .setListener(null)
                }

                override fun onAnimationEnd(animation: Animator?) {
                    descriptionAnimation()
                }
            })

    }

    private fun checkUserLogin(){
        val currentUser = mAuth.currentUser
        if(currentUser != null){
            val editor = mSharedPref.edit()
            editor.putString("USER_ID",currentUser.uid)
            editor.putString("USER_IMAGE",currentUser.photoUrl.toString())
            editor.putString("USER_NAME",currentUser.displayName)
            editor.apply()
            editor.commit()

            val intent = Intent(this,HomeActivity::class.java)

            startActivity(intent)
            finish()
        }
        else{
            val intent = Intent(this,MainActivity::class.java)

            val options = ActivityOptions.makeSceneTransitionAnimation(this,
                Pair<View,String>(iv_logo_splash,"trans_logo"),
                Pair<View,String>(tv_greeting_splash,"trans_desc")
            ).toBundle()
            startActivity(intent,options)
            finish()
        }
    }


    private fun descriptionAnimation(){
        tv_greeting_splash.animate()
            .alpha(1f)
            .setDuration(shortAnimationDuration)
            .setListener(object : AnimatorListenerAdapter(){
                override fun onAnimationStart(animation: Animator?) {
                    logoAnimation()
                }
            })
    }

    private fun logoAnimation() {
        progressbar_splash.visibility = View.VISIBLE
        tv_checkLogin_splash.visibility = View.VISIBLE
        iv_logo_splash.animate()
            .alpha(1f)
            .setDuration(shortAnimationDuration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    checkUserLogin()
                }
            })
    }

}