package com.emtwnty.schemaker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.model.online.GroupModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_group_rv.view.*

class GroupsAdapter:RecyclerView.Adapter<GroupsAdapter.ViewHolder>() {

    private lateinit var listGroup: List<GroupModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_group_rv,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return listGroup.size
    }

    override fun onBindViewHolder(holder: GroupsAdapter.ViewHolder, position: Int) {
        holder.bind(listGroup.get(position))
    }

    fun groupsAdapter(listGroup: List<GroupModel>){
        this.listGroup = listGroup
    }

    inner class ViewHolder(
        view:View
    ): RecyclerView.ViewHolder(view){
        val tvTitle = view.tv_titleGroup_layoutGroup
        val ivGroup = view.iv_groupImage_layoutGroup
        fun bind(groupModel: GroupModel){
            tvTitle.text = groupModel.groupName
            if(groupModel.groupImage != "null"){
                Picasso.get().load(groupModel.groupImage).into(ivGroup)
            }
        }
    }
}