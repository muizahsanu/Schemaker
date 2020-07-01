package com.example.schemaker.model

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class ScheduleRepo(app: Application): CoroutineScope {

    private var mScheduleDao: ScheduleDao?

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    init {
        val db = SchemakerDB.getInstance(app)
        mScheduleDao = db.scheduleDao
    }

    fun getData() = mScheduleDao?.getAllData()
    fun getListRowReminders() = mScheduleDao?.getListRowReminders()

    fun deleteAllData(){launch { deleteAllDataBG() }}

    fun setData(scheduleEntity: ScheduleEntity){
        launch { setDataBackground(scheduleEntity) }
    }

    private suspend fun setDataBackground(scheduleEntity: ScheduleEntity){
        withContext(Dispatchers.IO){
            mScheduleDao?.upsertSchedule(scheduleEntity)
        }
    }

    private suspend fun deleteAllDataBG(){
        withContext(Dispatchers.IO){
            mScheduleDao?.deleteAllData()
        }
    }
}