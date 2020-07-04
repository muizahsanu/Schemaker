package com.emtwnty.schemaker.adapter

import android.graphics.Color
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.model.ScheduleEntity
import kotlinx.android.synthetic.main.layout_schedule_rv.view.*
import java.util.*

class ScheduleAdapter:RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    private lateinit var scheduleItems: List<ScheduleEntity>
    private lateinit var mItemClickListener: ItemClickListener

    inner class ViewHolder(v:View):RecyclerView.ViewHolder(v){
        val titleSche = v.tv_title_layoutRV
        val descSche = v.tv_desc_layoutRV
        val linBackground = v.lin_layourRV
        val dateSche = v.tv_date_layoutRV
        val timeSche = v.tv_time_layoutRV

        fun bind(scheduleEntity: ScheduleEntity, clickListener: ItemClickListener){
            titleSche.text = scheduleEntity.title
            descSche.text = scheduleEntity.description
            linBackground.setCardBackgroundColor(Color.parseColor(scheduleEntity.bgcolor))
            val dateTime = Calendar.getInstance()
            dateTime.timeInMillis = scheduleEntity.timestamp.toLong() * 1000L
            dateSche.text = DateFormat.format("dd MMM yyyy",dateTime).toString()
            if(scheduleEntity.with_time == true){
                timeSche.text = DateFormat.format("hh:mm a",dateTime).toString()
            }
            else{
                timeSche.text = "All day"
            }

            // Click item listener
            itemView.setOnClickListener {
                clickListener.itemClickListener(scheduleEntity)
            }
            itemView.setOnLongClickListener(object: View.OnLongClickListener{
                override fun onLongClick(p0: View?): Boolean {
                    clickListener.itemLongClickListener(scheduleEntity)
                    return true
                }
            })
        }
    }

    fun scheduleAdapter(scheduleEntity: List<ScheduleEntity>,clickListener: ItemClickListener){
        scheduleItems = scheduleEntity
        mItemClickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_schedule_rv,parent,false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(scheduleItems.get(position), mItemClickListener)
    }

    override fun getItemCount(): Int {
        return scheduleItems.size
    }

    interface ItemClickListener {
        fun itemClickListener(scheduleEntity: ScheduleEntity)
        fun itemLongClickListener(scheduleEntity: ScheduleEntity)
    }
}