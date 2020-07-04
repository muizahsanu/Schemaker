package com.emtwnty.schemaker.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ScheduleDao {
    /** [---UPDATE & INSERT---] **/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSchedule(scheduleEntity: ScheduleEntity)
    @Query("update schedule set done= :newDone where scheduleID = :scID")
    suspend fun updateRemindMe(scID: String, newDone:Boolean)


    /** [---DELETE---] **/
    @Delete
    suspend fun deleteOne(scheduleEntity: ScheduleEntity)
    @Query("delete from schedule")
    suspend fun deleteAllData()
    @Query("delete from schedule where scheduleID = :scheID")
    suspend fun deletByID(scheID: String)


    /** [---GET DATA---] **/
    @Query("Select * from schedule where done = 0 order by timestamp asc")
    fun getDonetask(): LiveData<List<ScheduleEntity>>
    @Query("Select * from schedule where remindMe = 1 order by timestamp asc")
    fun getRemindTask(): LiveData<List<ScheduleEntity>>
    @Query("Select * from schedule where done = 1 order by timestamp asc")
    fun getNotDoneTask(): LiveData<List<ScheduleEntity>>
}