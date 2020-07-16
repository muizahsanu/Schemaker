package com.emtwnty.schemaker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.emtwnty.schemaker.model.online.GroupScheduleRepo
import com.emtwnty.schemaker.model.online.ScheduleOnlineModel

class GroupScheViewModel(app: Application): AndroidViewModel(app) {

    private var repo: GroupScheduleRepo = GroupScheduleRepo

    fun addGroupSchedule(scheduleOnlineModel: ScheduleOnlineModel){
        repo.addGroupSchedule(scheduleOnlineModel)
    }

    fun scheduleResponseCallback():LiveData<String> = repo.scheduleResponseCallback

}