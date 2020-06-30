package com.example.schemaker.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ScheduleEntity::class],version = 2)
abstract class SchemakerDB: RoomDatabase() {

    abstract val scheduleDao: ScheduleDao

    companion object{
        @Volatile
        private var INSTANCE: SchemakerDB? = null
        fun getInstance(context: Context): SchemakerDB{
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SchemakerDB::class.java,
                        "schemakerDB").fallbackToDestructiveMigration().build()
                }
                return instance
            }
        }
    }
}