package com.phat.trackerapp.data.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.phat.trackerapp.data.model.BloodSugar

@Dao
interface BloodSugarDao {

    @Query("SELECT * FROM blood_sugar")
    fun getAll(): List<BloodSugar>

    @Insert(onConflict = REPLACE)
    fun insertAll(favourites: List<BloodSugar>)

    @Insert(onConflict = REPLACE)
    fun insert(heartRate: BloodSugar)

    @Delete
    fun delete(heartRate: BloodSugar)

    @Query("DELETE FROM blood_sugar")
    fun deleteAllTracker()

    @Query("select * from blood_sugar where id = :id")
    fun findBloodSugarById(id: Int): BloodSugar

    @Update
    fun update(heartRate: BloodSugar)
}