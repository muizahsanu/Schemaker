package com.example.schemaker.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertSchedule(scheduleEntity: ScheduleEntity)

    @Delete
    fun delete(scheduleEntity: ScheduleEntity)

    @Query("delete from schedule")
    fun deleteAllData()

    @Query("select * from schedule order by timestamp asc")
    fun getAllData(): LiveData<List<ScheduleEntity>>
}