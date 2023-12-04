package com.phat.trackerapp.data.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.phat.trackerapp.data.model.TimeAlarm

@Dao
interface TimeAlarmDao {

    @Query("SELECT * FROM TimeAlarm")
    fun getAll(): List<TimeAlarm>

    @Query("SELECT * FROM TimeAlarm ORDER BY time ASC")
    fun getAllByAscHour(): List<TimeAlarm>

    @Insert(onConflict = REPLACE)
    fun insert(timeAlarm: TimeAlarm)

    @Delete
    fun delete(timeAlarm: TimeAlarm)

    @Query("DELETE FROM TimeAlarm")
    fun deleteAllTimeAlarm()

    @Query("select * from TimeAlarm where id = :idTimeAlarm")
    fun findTimeAlarmById(idTimeAlarm: Int): TimeAlarm

    @Query("select * from TimeAlarm where time = :time")
    fun getTimeAlarmsByTime(time: Int): List<TimeAlarm>

    @Update
    fun update(timeAlarm: TimeAlarm)
}