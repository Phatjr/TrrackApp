package com.phat.trackerapp.data.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.phat.trackerapp.data.model.HeartRate

@Dao
interface HeartRateDao {

    @Query("SELECT * FROM heart_rate")
    fun getAll(): List<HeartRate>

    @Insert(onConflict = REPLACE)
    fun insertAll(favourites: List<HeartRate>)

    @Insert(onConflict = REPLACE)
    fun insert(heartRate: HeartRate)

    @Delete
    fun delete(heartRate: HeartRate)

    @Query("DELETE FROM heart_rate")
    fun deleteAllTracker()

    @Query("select * from heart_rate where id = :id")
    fun findHeartRateById(id: Int): HeartRate

    @Update
    fun update(heartRate: HeartRate)
}