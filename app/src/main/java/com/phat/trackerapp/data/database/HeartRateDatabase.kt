package com.phat.trackerapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.phat.trackerapp.data.dao.HeartRateDao
import com.phat.trackerapp.data.model.HeartRate

@Database(entities = [HeartRate::class], version = 1)
abstract class HeartRateDatabase : RoomDatabase() {

    companion object {

        @Volatile
        private var INSTANCE: HeartRateDatabase? = null

        fun getInstance(context: Context): HeartRateDatabase {

            if (INSTANCE != null) return INSTANCE!!

            synchronized(this) {

                INSTANCE = Room
                    .databaseBuilder(context, HeartRateDatabase::class.java, "HEART_RATE_DATABASE")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()

                return INSTANCE!!

            }
        }
    }

    abstract fun heartRateDao(): HeartRateDao
}