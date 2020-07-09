package com.emtwnty.schemaker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.emtwnty.schemaker.model.online.GroupListener
import com.emtwnty.schemaker.model.online.GroupModel
import com.emtwnty.schemaker.model.online.GroupRepo

class GroupViewModel(app: Application):AndroidViewModel(app) {

    private var repo: GroupRepo = GroupRepo
    private var groupListener: GroupListener? = null

    fun addGroup(groupMap: HashMap<String,Any>){
        repo.addGroup(groupMap)
    }

    fun result():LiveData<String> = repo.responseCallback
}