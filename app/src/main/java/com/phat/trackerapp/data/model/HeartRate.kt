package com.phat.trackerapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat

@Entity(tableName = "heart_rate")
class HeartRate(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var heartRate: Int, var date: String, var time: String, var tag: String = ""
) {
    fun getTimeLong(): Long {
        val format = SimpleDateFormat("HH:mm dd/MM/yyyy")
        return format.parse("$time $date").time
    }
}