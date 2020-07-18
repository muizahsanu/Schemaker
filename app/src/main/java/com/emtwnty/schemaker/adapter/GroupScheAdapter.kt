package com.emtwnty.schemaker.adapter

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.model.online.ScheduleOnlineModel
import kotlinx.android.synthetic.main.layout_groupsche_rv.view.*
import java.util.*
import kotlin.collections.ArrayList

class GroupScheAdapter:RecyclerView.Adapter<GroupScheAdapter.ViewHolder>(), Filterable {

    private lateinit var groupScheList: List<ScheduleOnlineModel>
    private lateinit var allGroupScheList: List<ScheduleOnlineModel>

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val tv_schetitle = view.tv_scheduleTitle_groupScheLayout
        val tv_schetanggal = view.tv_scheduleTanggal_groupScheLayout
        val tv_schejam = view.tv_scheduleJam_groupScheLayout

        fun onBind(scheduleOnlineModel: ScheduleOnlineModel){
            tv_schetitle.text = scheduleOnlineModel.title
            val timestamp = Calendar.getInstance()
            timestamp.timeInMillis = scheduleOnlineModel.timestamp * 1000L
            tv_schetanggal.text = DateFormat.format("dd MMM yyyy",timestamp).toString()
            tv_schejam.text = DateFormat.format("hh:mm a",timestamp).toString()
        }
    }

    fun groupScheAdapter(groupScheList: List<ScheduleOnlineModel>){
        this.groupScheList = groupScheList
        allGroupScheList = ArrayList<ScheduleOnlineModel>(groupScheList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_groupsche_rv,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return groupScheList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(groupScheList.get(position))
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val filteredList = ArrayList<ScheduleOnlineModel>()
                val filterPattern = p0.toString().toLowerCase().trim()
                for(row in allGroupScheList){
                    if(row.groupID.toLowerCase().contains(filterPattern)){
                        filteredList.add(row)
                    }
                }
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                groupScheList = p1?.values as List<ScheduleOnlineModel>
                notifyDataSetChanged()
            }
        }
    }
}