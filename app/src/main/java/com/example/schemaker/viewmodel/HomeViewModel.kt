package com.example.schemaker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.schemaker.model.ScheduleEntity
import com.example.schemaker.model.ScheduleRepo

class HomeViewModel(app: Application): AndroidViewModel(app) {

    private var repo: ScheduleRepo = ScheduleRepo(app)

    fun getAllData() = repo.getData()

    fun setData(scheduleEntity: ScheduleEntity){
        repo.setData(scheduleEntity)
    }

}