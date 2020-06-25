package com.example.schemaker.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertSchedule(scheduleEntity: ScheduleEntity)

    @Query("select * from schedule")
    fun getAllData(): LiveData<List<ScheduleEntity>>
}