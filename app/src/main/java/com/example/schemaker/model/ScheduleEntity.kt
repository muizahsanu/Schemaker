package com.example.schemaker.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "schedule")
data class ScheduleEntity (
    @PrimaryKey
    val scheduleID: String,
    val title: String,
    val description: String,
    val timestamp: String,
    val bgcolor:String,
    val with_time: Boolean,
    val remindMe: Boolean
) : Parcelable