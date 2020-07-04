package com.emtwnty.schemaker.model

import android.app.Application
import com.google.firebase.database.DatabaseReference
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

    fun updateRemindMe(scheID: String, newRemindMe: Boolean){launch { updateRemindMeBG(scheID, newRemindMe) }}

    fun getData() = mScheduleDao?.getAllData()
    fun getListRowReminders() = mScheduleDao?.getListRowReminders()

    fun deleteAllData(){launch { deleteAllDataBG() }}
    fun deleteOne(scheduleEntity: ScheduleEntity){launch { deleteOneBG(scheduleEntity) }}
    fun deleteByID(scheID: String){launch { deleteByIDBG(scheID) }}

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

    private suspend fun updateRemindMeBG(scheID: String, newRemindMe: Boolean){
        withContext(Dispatchers.IO){
            mScheduleDao?.updateRemindMe(scheID,newRemindMe)
        }
    }
}