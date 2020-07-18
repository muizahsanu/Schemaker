package com.emtwnty.schemaker.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import com.emtwnty.schemaker.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_viewuser_dialog.view.*

private const val USER_NAME = "USER_NAME"
private const val USER_IMAGE = "USER_IMAGE"
private const val OLD_ROLE = "OLD_ROLE"
private const val USER_ID = "USER_ID"
private const val ROLE_CURRENT_USER = "ROLE_CURRENT_USER"

class DialogUser: DialogFragment() {
    private var userName: String? = null
    private var userImage: String? = null
    private var mOldRole: String? = null
    private var userID: String? = null
    private var roleCurrentUser: String? = null

    private lateinit var v:View

    companion object {
        @JvmStatic
        fun newInstance(userName: String, userImage:String, oldRole: String,userID: String
                        , roleCurrentUser: String) =
            DialogUser().apply {
                arguments = Bundle().apply {
                    putString(USER_NAME, userName)
                    putString(USER_IMAGE,userImage)
                    putString(OLD_ROLE,oldRole)
                    putString(USER_ID,userID)
                    putString(ROLE_CURRENT_USER,roleCurrentUser)
                }
            }
    }

    interface ResultSubmit{
        fun resultSubmit(newRole: String,oldRole: String,userID: String)
        fun kickMember(userID:String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userName = it.getString(USER_NAME)
            userImage = it.getString(USER_IMAGE)
            mOldRole = it.getString(OLD_ROLE)
            userID = it.getString(USER_ID)
            roleCurrentUser = it.getString(ROLE_CURRENT_USER)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.layout_viewuser_dialog,container,false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var newRole = "null"
        checkRole(view)

        if(roleCurrentUser == "hokage"){
            view.btn_kickUser_dialogUser.visibility = View.VISIBLE
            view.lin_role_dialogUser.visibility = View.VISIBLE
            view.lin_submit_dialogUser.visibility = View.VISIBLE
        }

        view.tv_userName_dialogUser.text = userName
        Picasso.get().load(userImage).into(view.iv_userImage_dialogUser)
        view.btn_cancel_dialogUser.setOnClickListener {
            dismiss()
        }

        view.btn_kickUser_dialogUser.setOnClickListener {
            val kickUser: ResultSubmit = targetFragment as ResultSubmit
            kickUser.kickMember(userID!!)
            dismiss()
        }

        view.radioGroup_role_dialogUser.setOnCheckedChangeListener(
            object : RadioGroup.OnCheckedChangeListener{
                override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
                    when(p1){
                        R.id.radioButton_hokage-> newRole = "hokage"
                        R.id.radioButton_chunin-> newRole = "chunin"
                        else-> newRole = "genin"
                    }
                }
            }
        )

        view.btn_submit_dialogUser.setOnClickListener {
            val mResultSubmit:ResultSubmit = targetFragment as ResultSubmit
            mResultSubmit.resultSubmit(newRole,mOldRole!!,userID!!)
            dismiss()
        }
    }

    private fun checkRole(view:View){
        if(mOldRole == "hokage"){
            view.radioButton_hokage.isChecked = true
        }else if(mOldRole == "chunin"){
            view.radioButton_chunin.isChecked = true
        }else{
            view.radioButton_genin.isChecked = true
        }
    }

}