package com.phat.trackerapp.callback

import com.phat.trackerapp.data.model.TimeAlarm

interface OnAlarmListener {
    fun onInsert(timeAlarm: TimeAlarm)
    fun onUpdate(timeAlarm: TimeAlarm)
    fun onDelete(timeAlarm: TimeAlarm)
}