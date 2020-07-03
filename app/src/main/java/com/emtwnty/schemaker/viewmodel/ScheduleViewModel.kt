package com.emtwnty.schemaker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.emtwnty.schemaker.model.ScheduleEntity
import com.emtwnty.schemaker.model.ScheduleRepo

class ScheduleViewModel(app: Application): AndroidViewModel(app) {

    private var repo: ScheduleRepo = ScheduleRepo(app)

    fun getAllData() = repo.getData()

    fun getRowsSchedule() = repo.getListRowReminders()

    fun setData(scheduleEntity: ScheduleEntity){
        repo.setData(scheduleEntity)
    }

    fun deleteAllData(){repo.deleteAllData()}

    fun deletOne(scheduleEntity: ScheduleEntity){
        repo.deleteOne(scheduleEntity)
    }

    fun deleteByID(scID: String){
        repo.deleteByID(scID)
    }

    fun updateRemindMe(scheID: String, newRemindMe: Boolean){
        repo.updateRemindMe(scheID, newRemindMe)
    }

}