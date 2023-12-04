package com.phat.trackerapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.phat.trackerapp.R
import java.text.SimpleDateFormat

@Entity(tableName = "blood_sugar")
class BloodSugar(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var value: Float,
    var unit: Float,
    var state: Int = 1,
    var date: String,
    var time: String,
    var typeState: Int = 0,
    var tag: String = ""
) {
    fun getTimeLong(): Long {
        val format = SimpleDateFormat("HH:mm dd/MM/yyyy")
        return format.parse("$time $date").time
    }

    companion object {
        fun getState(state: Int): String {
            return when (state) {
                0 -> "Low"
                1 -> "Normal"
                2 -> "Pre-diabetes"
                else -> "Diabetes"
            }
        }

        fun getTypeState(state: Int): Int {
            return when (state) {
                0 -> R.string.txt_default
                1 -> R.string.txt_during_fasting
                2 -> R.string.txt_before_eating
                3 -> R.string.txt_after_eating_1h
                4 -> R.string.txt_after_eating_2h
                5 -> R.string.txt_before_bedtime
                6 -> R.string.txt_before_workout
                else -> R.string.txt_after_wordkout
            }
        }

    }
}