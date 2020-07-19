package com.emtwnty.schemaker.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.ui.main.HomeActivity
import com.emtwnty.schemaker.viewmodel.GroupViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_invitation.*

class InvitationActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mGroupViewModel: GroupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invitation)


        mAuth = FirebaseAuth.getInstance()
        mGroupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)

        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener {
                var deepLink: Uri? = null
                if(it != null){
                    deepLink = it.link

                    val grouID = deepLink?.getQueryParameter("groupID")
                    if(mAuth.currentUser == null){
                        val intent = Intent(this,MainActivity::class.java)
                        intent.putExtra("GROUPID_FROMLINK",grouID)
                        startActivity(intent)
                        finish()
                    }else showDialogInvitation(grouID!!)
                }
            }
    }

    private fun showDialogInvitation(groupIDFromLink: String){
        val alertDialog = AlertDialog.Builder(this,R.style.DialogTheme)
        alertDialog
            .setTitle("Invitation")
            .setMessage("Join the group?")
            .setPositiveButton("Join",
            DialogInterface.OnClickListener { dialogInterface, i ->
                // Join The group
                mGroupViewModel.addMemberGroup(groupIDFromLink)
                indicator()
            })
            .setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialogInterface, i ->
                //  Cancel dan tidak join group
                dialogInterface.cancel()
                finish()
                System.exit(0)
            })
        alertDialog.create().show()
    }

    private fun indicator(){
        mGroupViewModel.result().observe(this, Observer {
            when(it){
                "PROSES_ADDMEMBER"-> progressbar_invitation.visibility = View.VISIBLE
                "FINISH_JOIN_GROUP"-> {
                    Toast.makeText(this,"You have successfully joined the group", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        })
    }
}