package com.phat.trackerapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.phat.trackerapp.data.dao.BMIDao
import com.phat.trackerapp.data.model.BMI

@Database(entities = [BMI::class], version = 1)
abstract class BMIDatabase : RoomDatabase() {

    companion object {

        @Volatile
        private var INSTANCE: BMIDatabase? = null

        fun getInstance(context: Context): BMIDatabase {

            if (INSTANCE != null) return INSTANCE!!

            synchronized(this) {

                INSTANCE = Room
                    .databaseBuilder(context, BMIDatabase::class.java, "BMI_DATABASE")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()

                return INSTANCE!!

            }
        }
    }

    abstract fun BMIDao(): BMIDao
}