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
import com.emtwnty.schemaker.adapter.GroupScheAdapter
import com.emtwnty.schemaker.adapter.GroupsAdapter
import com.emtwnty.schemaker.model.online.ScheduleOnlineModel
import com.emtwnty.schemaker.viewmodel.GroupViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.fragment_group_schdule.*
import kotlinx.android.synthetic.main.fragment_group_schdule.view.*


private const val GROUP_ID = "param1"

class GroupSchduleFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var groupID: String? = null

    private lateinit var v:View
    private lateinit var mContext: Context
    private var mDatabase = FirebaseFirestore.getInstance()
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mGroupViewModel: GroupViewModel
    private lateinit var mGroupAdapter: GroupScheAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            groupID = it.getString(GROUP_ID)
        }
        mAuth = FirebaseAuth.getInstance()
        mGroupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)
        mGroupAdapter = GroupScheAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_group_schdule, container, false)

        getUserRole()
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.btn_addSchedule_scheduleFrag.setOnClickListener {
            val intent = Intent(mContext,AddScheOnlineActivity::class.java)
            intent.putExtra("GROUP_ID",groupID)
            startActivity(intent)
        }


        getGroupScheduleData()
    }

    private fun getGroupScheduleData(){
        mGroupViewModel.getAllDataSchedule().observe(this,
            Observer<ArrayList<ScheduleOnlineModel>>{
                if(it != null){
                    setDataGroupScheduleToRecView(it)
                }
            })
    }
    private fun setDataGroupScheduleToRecView(groupScheList: List<ScheduleOnlineModel>){
        mGroupAdapter.groupScheAdapter(groupScheList)
        v.rv_schedules_scheduleFrag.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = mGroupAdapter
        }
    }

    private fun getUserRole(){
        val userID = mAuth.currentUser?.uid.toString()
        mDatabase.collection("users").document(userID)
            .collection("groups").document(groupID!!).get().addOnSuccessListener {
                if(it != null){
                    val data = it.data as HashMap<*,*>
                    val role = data.get("role")
                    if(role != "hokage"){
                        btn_addSchedule_scheduleFrag.visibility = View.GONE
                    }
                }
            }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            GroupSchduleFragment().apply {
                arguments = Bundle().apply {
                    putString(GROUP_ID, param1)
                }
            }
    }
}