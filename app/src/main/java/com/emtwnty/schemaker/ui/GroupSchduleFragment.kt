package com.emtwnty.schemaker.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.adapter.GroupScheAdapter
import com.emtwnty.schemaker.model.online.GroupScheModel
import com.emtwnty.schemaker.ui.dialog.DialogDeleteGroupSche
import com.emtwnty.schemaker.ui.group.AddScheOnlineActivity
import com.emtwnty.schemaker.ui.group.GroupScheDetailActivity
import com.emtwnty.schemaker.viewmodel.GroupScheViewModel
import com.emtwnty.schemaker.viewmodel.GroupViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_group.*
import kotlinx.android.synthetic.main.fragment_group_schdule.view.*


private const val GROUP_ID = "GROUP_ID"
private const val CURRENT_USER_ROLE = "CURRENT_USER_ROLE"

class GroupSchduleFragment : Fragment(), GroupScheAdapter.ItemClickListener {
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

        if (currentUserRole == "hokage" || currentUserRole == "chunin") {
            view.btn_addSchedule_scheduleFrag.visibility = View.VISIBLE
        }
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
        mGroupScheaAdapter.groupScheAdapter(groupScheList,this)
        v.rv_schedules_scheduleFrag.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = mGroupScheaAdapter
        }
    }

    override fun itemClickLister(groupScheModel: GroupScheModel) {
        val intent = Intent(context,GroupScheDetailActivity::class.java)
        intent.putExtra("TITLE",groupScheModel.title)
        intent.putExtra("DESC",groupScheModel.description)
        intent.putExtra("TIMESTAMP",groupScheModel.timestamp)
        intent.putExtra("GROUP_ID",groupScheModel.groupID)
        intent.putExtra("SCHEDULE_ID",groupScheModel.scheduleID)
        intent.putExtra("DONE",groupScheModel.done)
        startActivity(intent)
    }

    override fun itemLongClickListener(groupScheModel: GroupScheModel) {
        val scheduleID = groupScheModel.scheduleID
        val dialogDelete: DialogFragment = DialogDeleteGroupSche.newInstance(scheduleID)
        dialogDelete.show(fragmentManager!!,"dialog-fragment_delete-schedule")
    }
}