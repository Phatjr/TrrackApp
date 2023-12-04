package com.phat.trackerapp.data.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.phat.trackerapp.data.model.Tracker

@Dao
interface TrackerDao {
    
    @Query("SELECT * FROM tracker")
    fun getAll(): List<Tracker>

    @Insert(onConflict = REPLACE)
    fun insertAll(favourites: List<Tracker>)

    @Insert(onConflict = REPLACE)
    fun insert(tracker: Tracker)

    @Delete
    fun delete(tracker: Tracker)

    @Query("DELETE FROM tracker")
    fun deleteAllTracker()

    @Query("select * from tracker where id = :idTracker")
    fun findTrackerById(idTracker: Int): Tracker

    @Update
    fun update(tracker: Tracker)
}