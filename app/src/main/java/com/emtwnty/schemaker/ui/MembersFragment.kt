package com.emtwnty.schemaker.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.adapter.MembersAdapter
import com.emtwnty.schemaker.model.online.UsersModel
import com.emtwnty.schemaker.viewmodel.MembersViewModel
import com.emtwnty.schemaker.viewmodel.UsersViewModel
import kotlinx.android.synthetic.main.fragment_members.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val GROUP_ID = "null"

class MembersFragment : Fragment(),MembersAdapter.ItemClickListener {
    // TODO: Rename and change types of parameters
    private var groupID: String? = null

    private lateinit var mView:View
    private lateinit var mContext: Context

    private lateinit var mMembersAdapter: MembersAdapter
    private lateinit var mMembersViewModel: MembersViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            groupID = it.getString(GROUP_ID)
        }
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
        mMembersViewModel = ViewModelProviders.of(this).get(MembersViewModel::class.java)
        getDataMember()

        // Click listener
        mView.btn_inviteMember_memveberFrag.setOnClickListener {
            val intent = Intent(mContext,FindUserActivity::class.java)
            intent.putExtra("GROUP_ID",groupID.toString())
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
    }

    fun getDataMember(){
        mMembersViewModel.memberDataFromGroup(groupID.toString()).observe(this,
            Observer<List<UsersModel>>{
                println("members_fragment => ${it}")
                setRecyclerView(it)
            }
        )
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
        TODO("Not yet implemented")
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