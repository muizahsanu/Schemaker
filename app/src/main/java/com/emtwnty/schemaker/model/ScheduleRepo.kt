package com.emtwnty.schemaker.model

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

    fun updateRemindMe(scheID: String, newDone: Boolean){launch { updateRemindMeBG(scheID, newDone) }}

    fun getDonetask() = mScheduleDao?.getDonetask()
    fun getRemindTask() = mScheduleDao?.getRemindTask()
    fun getNotDoneTask() = mScheduleDao?.getNotDoneTask()

    fun deleteAllData(){launch { deleteAllDataBG() }}
    fun deleteOne(scheduleEntity: ScheduleEntity){launch { deleteOneBG(scheduleEntity) }}
    fun deleteByID(scheID: String){launch { deleteByIDBG(scheID) }}
    fun deleteDoneTask(){launch { deleteDoneTaskBG() }}

    fun setData(scheduleEntity: ScheduleEntity){
        launch { setDataBackground(scheduleEntity) }
    }

    private suspend fun deleteAllDataBG(){
        withContext(Dispatchers.IO){
            mScheduleDao?.deleteAllData()
        }
    }
    private suspend fun deleteOneBG(scheduleEntity: ScheduleEntity){
        withContext(Dispatchers.IO){
            mScheduleDao?.deleteOne(scheduleEntity)
        }
    }
    private suspend fun deleteByIDBG(scheID: String){
        withContext(Dispatchers.IO){
            mScheduleDao?.deletByID(scheID)
        }
    }
    private suspend fun deleteDoneTaskBG(){
        withContext(Dispatchers.IO){
            mScheduleDao?.deleteDoneTask()
        }
    }

    private suspend fun setDataBackground(scheduleEntity: ScheduleEntity){
        withContext(Dispatchers.IO){
            mScheduleDao?.upsertSchedule(scheduleEntity)
        }
    }
    private suspend fun updateRemindMeBG(scheID: String, newDone: Boolean){
        withContext(Dispatchers.IO){
            mScheduleDao?.updateRemindMe(scheID,newDone)
        }
    }
}