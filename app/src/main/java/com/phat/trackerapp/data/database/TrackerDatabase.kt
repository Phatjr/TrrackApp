package com.phat.trackerapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.phat.trackerapp.data.dao.TrackerDao
import com.phat.trackerapp.data.model.Tracker

@Database(entities = [Tracker::class], version = 1)
abstract class TrackerDatabase : RoomDatabase() {

    companion object {

        @Volatile
        private var INSTANCE: TrackerDatabase? = null

        fun getInstance(context: Context): TrackerDatabase {

            if (INSTANCE != null) return INSTANCE!!

            synchronized(this) {

                INSTANCE = Room
                    .databaseBuilder(context, TrackerDatabase::class.java, "TRACKER_DATABASE")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()

                return INSTANCE!!

            }
        }
    }

    abstract fun trackerDao(): TrackerDao
}