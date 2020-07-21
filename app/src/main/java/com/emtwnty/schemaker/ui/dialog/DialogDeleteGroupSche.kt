package com.emtwnty.schemaker.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.viewmodel.GroupScheViewModel
import kotlinx.android.synthetic.main.layout_deleteschedule_dialog.view.*


const val SCHEDULE_ID = "SCHEDULE_ID"

class DialogDeleteGroupSche: DialogFragment() {
    private var scheduleID: String? = null

    private lateinit var mContext: Context
    private lateinit var mGroupScheViewModel: GroupScheViewModel

    companion object{
        @JvmStatic
        fun newInstance(scheduleID: String) =
            DialogDeleteGroupSche().apply {
                arguments = Bundle().apply {
                    putString(SCHEDULE_ID,scheduleID)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            scheduleID = it.getString(SCHEDULE_ID)
        }

        mGroupScheViewModel = ViewModelProviders.of(this).get(GroupScheViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_deleteschedule_dialog,container,false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.btn_cancel_deleteDialog.setOnClickListener {
            dismiss()
        }
        view.btn_delete_deletDialog.setOnClickListener {
            mGroupScheViewModel.deleteSchedule(scheduleID!!)
            dismiss()
        }
    }

}