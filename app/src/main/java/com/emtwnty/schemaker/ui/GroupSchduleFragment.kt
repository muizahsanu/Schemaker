package com.emtwnty.schemaker.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.emtwnty.schemaker.R
import kotlinx.android.synthetic.main.fragment_group_schdule.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val GROUP_ID = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [GroupSchduleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GroupSchduleFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var groupID: String? = null

    private lateinit var v:View
    private lateinit var mContext: Context

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
            val intent = Intent(mContext,AddScheOnlineActivity::class.java)
            intent.putExtra("GROUP_ID",groupID)
            startActivity(intent)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GroupSchduleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            GroupSchduleFragment().apply {
                arguments = Bundle().apply {
                    putString(GROUP_ID, param1)
                }
            }
    }
}