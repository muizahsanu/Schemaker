package com.example.schemaker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.schemaker.model.ScheduleEntity
import com.example.schemaker.model.ScheduleRepo

class ScheduleViewModel(app: Application): AndroidViewModel(app) {

    private var repo: ScheduleRepo = ScheduleRepo(app)

    fun getAllData() = repo.getData()

    fun getRowsSchedule() = repo.getListRowReminders()

    fun setData(scheduleEntity: ScheduleEntity){
        repo.setData(scheduleEntity)
    }

    fun deleteAllData(){repo.deleteAllData()}

}