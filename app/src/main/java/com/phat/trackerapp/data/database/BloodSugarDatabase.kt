package com.phat.trackerapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.phat.trackerapp.data.dao.BloodSugarDao
import com.phat.trackerapp.data.model.BloodSugar

@Database(entities = [BloodSugar::class], version = 1)
abstract class BloodSugarDatabase : RoomDatabase() {

    companion object {

        @Volatile
        private var INSTANCE: BloodSugarDatabase? = null

        fun getInstance(context: Context): BloodSugarDatabase {

            if (INSTANCE != null) return INSTANCE!!

            synchronized(this) {

                INSTANCE = Room
                    .databaseBuilder(context, BloodSugarDatabase::class.java, "BLOOD_SUGAR_DATABASE")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()

                return INSTANCE!!

            }
        }
    }

    abstract fun bloodSugarDao(): BloodSugarDao
}