package com.emtwnty.schemaker.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.model.online.GroupModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_group_rv.view.*

class GroupsAdapter:RecyclerView.Adapter<GroupsAdapter.ViewHolder>(), Filterable {

    private var listGroup: List<GroupModel> = ArrayList()
    private lateinit var simpanListGroup: List<GroupModel>
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
    }

    fun groupsAdapter(listGroup: List<GroupModel>,clickListener: onItemClickListener){
        this.listGroup = listGroup
        simpanListGroup = ArrayList<GroupModel>(listGroup)
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

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val filterGroupList = ArrayList<GroupModel>()
                val filterPattern = p0.toString().toLowerCase().trim()
                for (row in simpanListGroup){
                    if(row.groupName.toLowerCase().trim().contains(filterPattern)){
                        filterGroupList.add(row)
                    }
                }
                val results = FilterResults()
                results.values = filterGroupList
                return results
            }
            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                listGroup = p1?.values as List<GroupModel>
                notifyDataSetChanged()
            }
        }
    }
}