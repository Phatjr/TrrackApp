package com.phat.trackerapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat

@Entity(tableName = "tracker")
class Tracker(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var time: String,
    var date: String,
    var systolic: Int,
    var diastolic: Int,
    var pulse: Int,
    var state: String,
    var tag: String
) {

    fun getTimeLong():Long {
        val format = SimpleDateFormat("HH:mm dd/MM/yyyy")
        return format.parse("$time $date").time
    }

}