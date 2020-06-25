package com.example.schemaker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.schemaker.R
import com.example.schemaker.model.ScheduleEntity
import kotlinx.android.synthetic.main.layout_schedule_rv.view.*

class ScheduleAdapter:RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    private lateinit var scheduleItems: List<ScheduleEntity>

    inner class ViewHolder(v:View):RecyclerView.ViewHolder(v){
        val titleSche = v.tv_title_layoutRV
        val descSche = v.tv_desc_layoutRV

        fun bind(scheduleEntity: ScheduleEntity){
            titleSche.text = scheduleEntity.title
            descSche.text = scheduleEntity.description
        }
    }

    fun scheduleAdapter(scheduleEntity: List<ScheduleEntity>){
        scheduleItems = scheduleEntity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_schedule_rv,parent,false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(scheduleItems.get(position))
    }

    override fun getItemCount(): Int {
        return scheduleItems.size
    }
}