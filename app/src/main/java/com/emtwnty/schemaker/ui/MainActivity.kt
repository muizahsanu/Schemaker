package com.emtwnty.schemaker.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.model.online.UsersModel
import com.emtwnty.schemaker.ui.main.HomeActivity
import com.emtwnty.schemaker.viewmodel.UsersViewModel
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
    private lateinit var mUsersViewModel: UsersViewModel

    private var ID_PREF = "com.emtwnty.schemaker-user"
    private lateinit var mSharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        mUsersViewModel = ViewModelProviders.of(this).get(UsersViewModel::class.java)
        mSharedPref = getSharedPreferences(ID_PREF, Context.MODE_PRIVATE)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)

        btn_getStarted_main.setOnClickListener {
            lin_progressbar_main.visibility = View.VISIBLE
            startActivity(Intent(this,
                HomeActivity::class.java))
            finish()
        }

        btn_signInGoogle_main.setOnClickListener {
            startSignInGoogle()
        }

        val groupIDFromLink = intent.getStringExtra("GROUPID_FROMLINK")
        if(groupIDFromLink != null){
            btn_getStarted_main.visibility = View.GONE
        }

    }

    override fun onStart() {
        super.onStart()
        window.enterTransition = null
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
        lin_progressbar_main.visibility = View.INVISIBLE
    }

    private fun firebaseAuthGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken,null)
        lin_progressbar_main.visibility = View.VISIBLE
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener{
                if(it.isSuccessful){
                    Log.d(TAG, "Sign In with Google:Succes")
                    val user = mAuth.currentUser
                    setDataToDatabase(user)
                    updateUI(user)
                }
                else{
                    Log.d(TAG, "Sign In with Google:",it.exception)
                    Toast.makeText(this,"Sign in Failed",Toast.LENGTH_SHORT).show()
                }
            }
        lin_progressbar_main.visibility = View.INVISIBLE
    }

    private fun setDataToDatabase(user: FirebaseUser?){
        val uid = user?.uid.toString()
        val email = user?.email.toString()
        val fullName = user?.displayName.toString()
        val imageUrl = user?.photoUrl.toString()
        val username = user?.email?.split("@")!!.get(0)
        val users = UsersModel(uid,email,username,fullName,imageUrl)
        mUsersViewModel.setData(users)

    }

    private fun startSignInGoogle(){
        lin_progressbar_main.visibility = View.VISIBLE
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun updateUI(user: FirebaseUser?){
        val groupIDFromLink = intent.getStringExtra("GROUPID_FROMLINK")
        if(user != null){
            val editor = mSharedPref.edit()
            editor.putString("USER_ID",user.uid)
            editor.putString("USER_IMAGE",user.photoUrl.toString())
            editor.putString("USER_NAME",user.displayName)
            editor.apply()
            editor.commit()


            val intent = Intent(this,HomeActivity::class.java)
            if(groupIDFromLink != null){
                intent.putExtra("GROUPID_FROMLINK",groupIDFromLink)
            }
            startActivity(intent)
            finish()
        }
    }

}
