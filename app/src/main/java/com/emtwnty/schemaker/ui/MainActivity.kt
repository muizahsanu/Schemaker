package com.emtwnty.schemaker.ui

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.util.Pair as UtilPair
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.ui.main.HomeActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object{
        private const val RC_SIGN_IN = 9001
        private const val TAG = "MainActivity"
    }

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)

        btn_login.setOnClickListener {
            val options = ActivityOptions.makeSceneTransitionAnimation(
                this,
                UtilPair.create(iv_logo_welcome, "image_logo"),
                UtilPair.create(linear_parent_main,"linear1"),
                UtilPair.create(linear_bottom_main,"linear2")
            )

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent, options.toBundle())
            overridePendingTransition(0,0)
        }

        btn_getStarted_main.setOnClickListener {
            startActivity(Intent(this,
                HomeActivity::class.java))
            finish()
        }

        btn_signInGoogle_main.setOnClickListener {
            startSignInGoogle()
        }
        btn_signOutGoogle_main.setOnClickListener{
            signOutGoolge()
        }

    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if(currentUser != null){
            Toast.makeText(this,"ADA",Toast.LENGTH_SHORT).show()
        }
        else Toast.makeText(this, "GAADA",Toast.LENGTH_SHORT).show()
        updateUI(currentUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG,"firebaseAuthWithGoogle:"+account.id)
                firebaseAuthGoogle(account.idToken!!)
            } catch (e: ApiException){
                Log.w(TAG,"Google sign in failed", e)
                updateUI(null)
            }
        }
    }

    private fun firebaseAuthGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken,null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener{
                if(it.isSuccessful){
                    Log.d(TAG, "Sign In with Google:Succes")
                    val user = mAuth.currentUser
                    updateUI(user)
                }
                else{
                    Log.d(TAG, "Sign In with Google:",it.exception)
                    Toast.makeText(this,"Sign in Failed",Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun startSignInGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOutGoolge(){
        mAuth.signOut()

        googleSignInClient.signOut().addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(this,"Sign out",Toast.LENGTH_SHORT).show()
                updateUI(null)
            }
        }
    }

    private fun updateUI(user: FirebaseUser?){
        if(user != null){
            tv_greeting_welcome.text = "Google Sign In : " + user.displayName
            tv_coba.text = "Firebase Sign In : " + user.photoUrl

            btn_signInGoogle_main.visibility = View.GONE
            btn_signOutGoogle_main.visibility = View.VISIBLE
        }
        else{
            btn_signInGoogle_main.visibility = View.VISIBLE
            btn_signOutGoogle_main.visibility = View.GONE
        }
    }

}
