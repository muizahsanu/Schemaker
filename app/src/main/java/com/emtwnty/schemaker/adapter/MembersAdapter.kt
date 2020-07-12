package com.emtwnty.schemaker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emtwnty.schemaker.R
import com.emtwnty.schemaker.model.online.UsersModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_members_rv.view.*

class MembersAdapter: RecyclerView.Adapter<MembersAdapter.ViewHolder>() {

    private lateinit var listMembers: List<UsersModel>
    private lateinit var mItemClick: ItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembersAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_members_rv,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return listMembers.size
    }

    override fun onBindViewHolder(holder: MembersAdapter.ViewHolder, position: Int) {
        holder.onBind(listMembers.get(position),mItemClick,position)
    }

    inner class ViewHolder(v:View): RecyclerView.ViewHolder(v){
        val iv_user = v.iv_userImage_membersLayout
        val tv_user = v.tv_userName_membersLayout

        fun onBind(usersModel: UsersModel, itemClick: ItemClickListener,position: Int){
            tv_user.text = usersModel.fullname
            Picasso.get().load(usersModel.imageURI).into(iv_user)

            itemView.setOnClickListener {
                itemClick.itemClickListener(usersModel,position)
            }
        }
    }

    fun membersAdapter(listMembers: List<UsersModel>, itemClick: ItemClickListener){
        this.listMembers = listMembers
        this.mItemClick = itemClick
    }

    interface ItemClickListener{
        fun itemClickListener(usersModel: UsersModel, position: Int)
    }
}