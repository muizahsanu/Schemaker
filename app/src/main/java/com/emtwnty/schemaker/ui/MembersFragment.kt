package com.emtwnty.schemaker.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.adapter.MembersAdapter
import com.emtwnty.schemaker.model.online.UsersModel
import com.emtwnty.schemaker.ui.dialog.DialogUser
import com.emtwnty.schemaker.viewmodel.GroupViewModel
import com.emtwnty.schemaker.viewmodel.MembersViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_members.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val GROUP_ID = "GROUP_ID"
private const val CURRENT_USER_ROLE = "CURRENT_USER_ROLE"

class MembersFragment : Fragment(),MembersAdapter.ItemClickListener, DialogUser.ResultSubmit {
    private var groupID: String? = null
    private var currentUserRole: String? = null

    private lateinit var mView:View
    private lateinit var mContext: Context

    private lateinit var mMembersAdapter: MembersAdapter
    private lateinit var mMembersViewModel: MembersViewModel
    private lateinit var mGroupViewMode: GroupViewModel
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth


    companion object {
        @JvmStatic
        fun newInstance(groupID: String,currentUserRole: String) =
            MembersFragment().apply {
                arguments = Bundle().apply {
                    putString(GROUP_ID, groupID)
                    putString(CURRENT_USER_ROLE, currentUserRole)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            groupID = it.getString(GROUP_ID)
            currentUserRole = it.getString(CURRENT_USER_ROLE)
        }

        mMembersViewModel = ViewModelProviders.of(this).get(MembersViewModel::class.java)
        mGroupViewMode = ViewModelProviders.of(this).get(GroupViewModel::class.java)
        mDatabase = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
    }


    override fun resultSubmit(newRole: String, oldRole: String,userID: String) {
        if(newRole != oldRole || newRole.isNotEmpty() || newRole != "null"){
            mMembersViewModel.updateRoleMember(groupID!!,userID,newRole)
        }
    }

    override fun kickMember(userID: String) {
        mMembersViewModel.kickMember(groupID!!,userID)
        mMembersViewModel.initGetUserData(groupID!!)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_members, container, false)
        return mView
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mMembersAdapter = MembersAdapter()

        // Click listener
        mView.btn_inviteMember_memveberFrag.setOnClickListener {
            val invitationLink = "https://schemaker.page.link/invite-group/?groupID=$groupID"
            Firebase.dynamicLinks.shortLinkAsync(ShortDynamicLink.Suffix.SHORT) {
                link = Uri.parse(invitationLink)
                domainUriPrefix = "https://schemaker.page.link"
                androidParameters("com.emtwnty.schemaker"){
                    minimumVersion = 1
                }
            }.addOnSuccessListener {
                val mInvitationURL = it.shortLink
                val intent = Intent()
                val msg = "Join Schemaker Group: $mInvitationURL"
                intent.setAction(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_TEXT,msg)
                intent.setType("text/plain")
                startActivity(intent)
                println("LINK_INVITE => $mInvitationURL")
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataMember()
    }

    fun getDataMember(){
        mMembersViewModel.getAllUserData().observe(this, Observer<ArrayList<UsersModel>>{
            if(it!=null){
                println("memberFrag: memberdata => ${it}")
                setRecyclerView(it)
            }
        })
    }
    fun setRecyclerView(listMembers: List<UsersModel>){
        val rv_members = mView.rv_members_membersFrag
        mMembersAdapter.membersAdapter(listMembers,this)
        rv_members.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = mMembersAdapter
        }
    }

    override fun itemClickListener(usersModel: UsersModel, position: Int) {
        mGroupViewMode.getUserRole(usersModel.uid,groupID!!).observe(this, Observer {
            if(currentUserRole != it){
                val dialogUser: DialogFragment = DialogUser.newInstance(
                    usersModel.fullname, usersModel.imageURI,it,usersModel.uid,currentUserRole!!)
                dialogUser.setTargetFragment(this@MembersFragment,300)
                dialogUser.show(fragmentManager!!,"dialogFragmet_user")
            }
        })
    }
}