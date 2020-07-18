package com.emtwnty.schemaker.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.model.online.GroupModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_group_rv.view.*

class GroupsAdapter:RecyclerView.Adapter<GroupsAdapter.ViewHolder>() {

    private lateinit var listGroup: List<GroupModel>
    private lateinit var mItemClickListener: onItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_group_rv,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return listGroup.size
    }

    override fun onBindViewHolder(holder: GroupsAdapter.ViewHolder, position: Int) {
        holder.bind(listGroup.get(position),mItemClickListener,position)
//        if(selected_position == position){
//            holder.cv_group.cardElevation = 1f
//            holder.cv_group.isClickable = false
//            holder.indicatorView.setBackgroundColor(Color.parseColor("#94194d"))
//        }else {
//            if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
//                holder.indicatorView.setBackgroundColor(Color.parseColor("#ffffff"))
//            }else{
//                holder.indicatorView.setBackgroundColor(Color.parseColor("#121212"))
//            }
//            holder.cv_group.cardElevation = 0F
//            holder.cv_group.isClickable = true
//        }
    }

    fun groupsAdapter(listGroup: List<GroupModel>,clickListener: onItemClickListener){
        this.listGroup = listGroup
        this.mItemClickListener = clickListener
    }

    inner class ViewHolder(
        view:View
    ): RecyclerView.ViewHolder(view){
        val tvTitle = view.tv_titleGroup_layoutGroup
        val ivGroup = view.iv_groupImage_layoutGroup
        fun bind(groupModel: GroupModel, clickListener: onItemClickListener,position: Int){
            tvTitle.text = groupModel.groupName
            if(groupModel.groupImage != "null"){
                Picasso.get().load(groupModel.groupImage).into(ivGroup)
            }

            itemView.setOnClickListener {
                clickListener.itemClickListener(groupModel,position)
            }
        }
    }

    interface onItemClickListener{
        fun itemClickListener(groupModel: GroupModel, position: Int)
    }
}