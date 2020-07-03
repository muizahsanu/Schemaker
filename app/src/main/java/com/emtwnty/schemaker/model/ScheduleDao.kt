package com.emtwnty.schemaker.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSchedule(scheduleEntity: ScheduleEntity)
    @Query("update schedule set remindMe= :newRemindMe where scheduleID = :scID")
    suspend fun updateRemindMe(scID: String, newRemindMe:Boolean)

    @Delete
    suspend fun deleteOne(scheduleEntity: ScheduleEntity)

    @Query("delete from schedule")
    suspend fun deleteAllData()

    @Query("delete from schedule where scheduleID = :scheID")
    suspend fun deletByID(scheID: String)

    @Query("Select * from schedule where remindMe = 0 order by timestamp asc")
    fun getAllData(): LiveData<List<ScheduleEntity>>

    @Query("Select * from schedule where remindMe = 1 order by timestamp asc")
    fun getListRowReminders(): LiveData<List<ScheduleEntity>>
}