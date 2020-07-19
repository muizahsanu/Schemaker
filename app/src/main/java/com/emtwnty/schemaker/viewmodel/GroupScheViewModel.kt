package com.emtwnty.schemaker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.emtwnty.schemaker.model.online.GroupScheduleRepo
import com.emtwnty.schemaker.model.online.GroupScheModel

class GroupScheViewModel(app: Application): AndroidViewModel(app) {

    private var repo: GroupScheduleRepo = GroupScheduleRepo

    fun addGroupSchedule(groupScheModel: GroupScheModel){
        repo.addGroupSchedule(groupScheModel)
    }

    fun getGroupSchedule():MutableLiveData<ArrayList<GroupScheModel>>{
        return repo.getAllGroupSche
    }
    fun initGetGroupSche(groupID: String){
        repo.initGetGroupSche(groupID)
    }

    fun scheduleResponseCallback():LiveData<String> = repo.scheduleResponseCallback
}