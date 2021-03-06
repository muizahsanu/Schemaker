package com.emtwnty.schemaker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.emtwnty.schemaker.model.ScheduleEntity
import com.emtwnty.schemaker.model.ScheduleRepo

class ScheduleViewModel(app: Application): AndroidViewModel(app) {

    private var repo: ScheduleRepo = ScheduleRepo(app)

    fun getDonetask() = repo.getDonetask()
    fun getRemindTask() = repo.getRemindTask()
    fun getNotDoneTask() = repo.getNotDoneTask()

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
    fun deleteDoneTask(){repo.deleteDoneTask()}

    fun updateRemindMe(scheID: String, newDone: Boolean){
        repo.updateRemindMe(scheID, newDone)
    }


    // ONLINE

}