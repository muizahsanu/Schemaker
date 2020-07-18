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
private const val GROUP_ID = "null"

class MembersFragment : Fragment(),MembersAdapter.ItemClickListener, DialogUser.ResultSubmit {
    private var groupID: String? = null

    private lateinit var mView:View
    private lateinit var mContext: Context

    private lateinit var mMembersAdapter: MembersAdapter
    private lateinit var mMembersViewModel: MembersViewModel
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    override fun resultSubmit(newRole: String, oldRole: String,userID: String) {
        if(newRole != oldRole || newRole.isNotEmpty() || newRole != "null"){
            val newRoleMap = HashMap<String,Any>()
            newRoleMap.put("role",newRole)
            mDatabase.collection("users").document(userID)
                .collection("groups").document(groupID!!)
                .set(newRoleMap).addOnCompleteListener {
                    if(it.isSuccessful){
                        mDatabase.collection("groups").document(groupID!!)
                            .collection("members").document(userID)
                            .set(newRoleMap)
                    }
                }
        }
    }

    override fun kickMember(userID: String) {
        mMembersViewModel.kickMember(groupID!!,userID)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            groupID = it.getString(GROUP_ID)
        }

        mMembersViewModel = ViewModelProviders.of(this).get(MembersViewModel::class.java)
        mDatabase = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
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
        val currentUserID = mAuth.currentUser?.uid.toString()
        CoroutineScope(IO).launch {
            if(usersModel.uid != currentUserID){
                val roleMap = mDatabase.collection("users").document(usersModel.uid)
                    .collection("groups").document(groupID!!).get().await().data
                val role = roleMap?.get("role").toString()

                val roleCurrentUserMap = mDatabase.collection("users").document(currentUserID)
                    .collection("groups").document(groupID!!).get().await().data
                val roleCurrentUser = roleCurrentUserMap?.get("role").toString()

                withContext(Main){
                    val dialogUser: DialogFragment = DialogUser.newInstance(
                        usersModel.fullname, usersModel.imageURI,role,usersModel.uid,roleCurrentUser)
                    dialogUser.setTargetFragment(this@MembersFragment,300)
                    dialogUser.show(fragmentManager!!,"dialogFragmet_user")
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            MembersFragment().apply {
                arguments = Bundle().apply {
                    putString(GROUP_ID, param1)
                }
            }
    }
}