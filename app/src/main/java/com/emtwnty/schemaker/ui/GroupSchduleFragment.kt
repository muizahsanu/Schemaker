package com.emtwnty.schemaker.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.adapter.GroupScheAdapter
import com.emtwnty.schemaker.model.online.GroupScheModel
import com.emtwnty.schemaker.ui.group.AddScheOnlineActivity
import com.emtwnty.schemaker.viewmodel.GroupScheViewModel
import com.emtwnty.schemaker.viewmodel.GroupViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_group.*
import kotlinx.android.synthetic.main.fragment_group_schdule.view.*


private const val GROUP_ID = "GROUP_ID"
private const val CURRENT_USER_ROLE = "CURRENT_USER_ROLE"

class GroupSchduleFragment : Fragment() {
    private var groupID: String? = null
    private var currentUserRole: String? = null

    private lateinit var v:View
    private lateinit var mContext: Context
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mGroupScheaAdapter: GroupScheAdapter
    private lateinit var mGroupScheViewModel: GroupScheViewModel

    companion object {
        @JvmStatic
        fun newInstance(groupID: String,currentUserRole: String) =
            GroupSchduleFragment().apply {
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


        mAuth = FirebaseAuth.getInstance()
        mGroupScheViewModel = ViewModelProviders.of(this).get(GroupScheViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_group_schdule, container, false)
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.btn_addSchedule_scheduleFrag.setOnClickListener {
            val intent = Intent(mContext,
                AddScheOnlineActivity::class.java)
            intent.putExtra("GROUP_ID",groupID)
            startActivity(intent)
        }

        if (currentUserRole == "hokage") {
            view.btn_addSchedule_scheduleFrag.visibility = View.VISIBLE
        }
        Toast.makeText(mContext,currentUserRole,Toast.LENGTH_SHORT).show()
        getListGroupScheduel()
    }

    private fun getListGroupScheduel(){
        mGroupScheViewModel.getGroupSchedule().observe(this,
            Observer<ArrayList<GroupScheModel>> {
                if(it != null){
                    setDataToRecyclerView(it)
                    println("data_schedule_fragment => $it")
                }
            })
    }

    private fun setDataToRecyclerView(groupScheList: ArrayList<GroupScheModel>){
        mGroupScheaAdapter = GroupScheAdapter()
        mGroupScheaAdapter.groupScheAdapter(groupScheList)
        v.rv_schedules_scheduleFrag.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = mGroupScheaAdapter
        }
    }
}