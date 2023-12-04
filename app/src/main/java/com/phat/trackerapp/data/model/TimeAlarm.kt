package com.phat.trackerapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(tableName = "TimeAlarm")
class TimeAlarm(
    @PrimaryKey(autoGenerate=true)
    var id: Int,
    var hour: Int,
    var minute: Int,
    var time: Int,
    var day1: Boolean,
    var day2: Boolean,
    var day3: Boolean,
    var day4: Boolean,
    var day5: Boolean,
    var day6: Boolean,
    var day7: Boolean,
    var typeAlarm: Int = 0
){
    fun stateTimeSuccess():Boolean {
        return !day1 && !day2 && !day3 && !day4 && !day5 && !day6 && !day7
    }

    fun stateDayCurrentOn(): Boolean {
        val day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        when(day) {
            2 -> {
                return day1
            }
            3 -> {
                return day2
            }
            4 -> {
                return day3
            }
            5 -> {
                return day4
            }
            6 -> {
                return day5
            }
            7 -> {
                return day6
            }
            else -> {
                return day7
            }
        }
    }
}