package com.example.schemaker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule")
data class ScheduleEntity (
    @PrimaryKey
    val scheduleID: String,
    val title: String,
    val description: String,
    val timestamp: String,
    val bgcolor:String,
    val with_time: Boolean
)