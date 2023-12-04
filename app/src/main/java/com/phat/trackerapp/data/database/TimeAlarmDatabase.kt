package com.phat.trackerapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.phat.trackerapp.data.dao.TimeAlarmDao
import com.phat.trackerapp.data.model.TimeAlarm

@Database(entities = [TimeAlarm::class], version = 2, exportSchema = true)
abstract class TimeAlarmDatabase : RoomDatabase() {

    companion object {

        @Volatile
        private var INSTANCE: TimeAlarmDatabase? = null

        fun getInstance(context: Context): TimeAlarmDatabase {
            if (INSTANCE != null) return INSTANCE!!

            synchronized(this) {

                INSTANCE = Room
                    .databaseBuilder(context, TimeAlarmDatabase::class.java, "TIME_ALARM_DATABASE")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()

                return INSTANCE!!

            }
        }
    }

    abstract fun timeAlarmDao(): TimeAlarmDao
}