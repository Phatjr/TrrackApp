package com.phat.trackerapp.data.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.phat.trackerapp.data.model.BMI

@Dao
interface BMIDao {

    @Query("SELECT * FROM bmi")
    fun getAll(): List<BMI>

    @Insert(onConflict = REPLACE)
    fun insertAll(favourites: List<BMI>)

    @Insert(onConflict = REPLACE)
    fun insert(heartRate: BMI)

    @Delete
    fun delete(heartRate: BMI)

    @Query("DELETE FROM bmi")
    fun deleteAllTracker()

    @Query("select * from bmi where id = :idBmi")
    fun findBMIById(idBmi: Int): BMI

    @Update
    fun update(heartRate: BMI)
}