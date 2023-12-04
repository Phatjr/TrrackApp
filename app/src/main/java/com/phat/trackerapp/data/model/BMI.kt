package com.phat.trackerapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat

@Entity(tableName = "bmi")
class BMI(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var weight: Float, var height: Float ,var date: String, var time: String, var tag: String = ""
) {
    fun getTimeLong():Long {
        val format = SimpleDateFormat("HH:mm dd/MM/yyyy")
        return format.parse("$time $date").time
    }
}