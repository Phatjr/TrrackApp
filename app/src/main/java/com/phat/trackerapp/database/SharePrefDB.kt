package com.phat.trackerapp.database

import android.app.Activity
import android.content.Context
import com.phat.trackerapp.R
import com.phat.trackerapp.screen.yourdata.model.User
import com.phat.trackerapp.utils.Constants

class SharePrefDB(var context: Context) {

    private var mSharePref = context.getSharedPreferences(Constants.DB_NAME, Activity.MODE_PRIVATE)

    private var mEditor = mSharePref.edit()

    companion object {
        var sharePrefDB: SharePrefDB? = null
        fun getInstance(context: Context): SharePrefDB {
            if (sharePrefDB == null) {
                sharePrefDB = SharePrefDB(context)
            }
            return sharePrefDB!!
        }
    }

    fun setUser(user: User){
        mEditor.putInt(Constants.GENDER, user.gender)
        mEditor.putInt(Constants.AGE, user.age)
        mEditor.putFloat(Constants.HEIGHT, user.height)
        mEditor.putFloat(Constants.WEIGHT, user.weight)
        mEditor.putInt(Constants.UNIT_HEIGHT, user.unitHeight)
        mEditor.putInt(Constants.UNIT_WEIGHT, user.unitWeight)
        mEditor.apply()
    }

    fun getUser(): User?{
        val gender = mSharePref.getInt(Constants.GENDER,0)
        val age = mSharePref.getInt(Constants.AGE,0)
        val height = mSharePref.getFloat(Constants.HEIGHT,0f)
        val weight = mSharePref.getFloat(Constants.WEIGHT,0f)
        val unitHeight = mSharePref.getInt(Constants.UNIT_HEIGHT,0)
        val unitWeight = mSharePref.getInt(Constants.UNIT_WEIGHT,0)
        if(age == 0 || height == 0f || weight == 0f) return null
        return User(gender,age, height, weight,unitHeight, unitWeight)
    }

    fun setAllNotes(key: String, value: Set<String>) {
        mEditor.putStringSet(key, value)
        mEditor.apply()
    }

    fun getAllNotes(key: String): Set<String> {
        return when(key){
            Constants.KEY_CHIP_NOTES -> mSharePref.getStringSet(key, listOf(
                context.getString(R.string.txt_signature),
                context.getString(R.string.txt_crazy),
                context.getString(R.string.txt_melt),
                context.getString(R.string.txt_run),
                context.getString(R.string.txt_lazy),
                context.getString(R.string.txt_diet),
                context.getString(R.string.txt_nightmare),
                context.getString(R.string.txt_right),
                context.getString(R.string.txt_left),
            ).toSet())!!
            Constants.KEY_BMI_NOTES -> mSharePref.getStringSet(key, listOf(
                context.getString(R.string.txt_after_break_fast),
                context.getString(R.string.txt_before_break_fast),
                context.getString(R.string.txt_after_lunch),
                context.getString(R.string.txt_before_lunch),
                context.getString(R.string.txt_after_dinner),
                context.getString(R.string.txt_before_dinner),
                context.getString(R.string.txt_morning),
            ).toSet())!!
            else ->  mSharePref.getStringSet(key, listOf(
                context.getString(R.string.txt_feeling_good),
                context.getString(R.string.txt_un_comfortable),
                context.getString(R.string.txt_oral_medication),
                context.getString(R.string.txt_insulin),
                context.getString(R.string.txt_pregnant),
                context.getString(R.string.txt_lactation),
                context.getString(R.string.txt_during_period),
            ).toSet())!!
        }
    }

}