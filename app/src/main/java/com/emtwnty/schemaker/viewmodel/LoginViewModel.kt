package com.emtwnty.schemaker.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.AndroidViewModel
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.ui.main.SettingActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginViewModel(app: Application): AndroidViewModel(app) {

    private var mAuth: FirebaseAuth
    private var LOGIN_RESULT: FirebaseUser?
    private var googleSignInClient: GoogleSignInClient
    private var mSharedPref: SharedPreferences
    var signInIntent: Intent

    companion object{
        const val ID_PREF = "com.emtwnty.schemaker-user"
    }


    init {
        mAuth = FirebaseAuth.getInstance()
        LOGIN_RESULT = null
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(app.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(app.applicationContext,gso)
        signInIntent = googleSignInClient.signInIntent
        mSharedPref = app.getSharedPreferences(ID_PREF,Context.MODE_PRIVATE)
    }

    fun signInGoogle(data: Intent?){
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthGoogle(account.idToken!!)
        }catch (e: ApiException){
            LOGIN_RESULT = null
        }
    }

     private fun firebaseAuthGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken,null)
        mAuth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful){
                val currentUser = mAuth.currentUser
                LOGIN_RESULT = currentUser
                setPrefUser(currentUser!!)
            }
        }
    }

    fun signOutUser(){
        mAuth.signOut()

        googleSignInClient.signOut().addOnCompleteListener {
            if(it.isSuccessful){
                LOGIN_RESULT = null
                val editor = mSharedPref.edit()
                editor.clear()
                editor.apply()
                editor.commit()
            }
        }
    }

    private fun setPrefUser(currentUser: FirebaseUser){
        val editor = mSharedPref.edit()
        editor.putString("USER_ID", currentUser.uid)
        editor.putString("USER_IMAGE", currentUser.photoUrl.toString())
        editor.putString("USER_NAME", currentUser.displayName)
        editor.apply()
        editor.commit()
    }

    fun loginResult(): FirebaseUser?{
        return LOGIN_RESULT
    }


}