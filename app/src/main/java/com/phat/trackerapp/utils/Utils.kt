package com.phat.trackerapp.utils

import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.TextUtils
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import java.util.Locale
import kotlin.math.roundToInt


object Utils {
    var onShow = false
    private var onceThru = true

    fun onCreateDialog(
        applicationContext: Context?,
        dialog_update_version: Int,
        isCanceledOnTouchOutside: Boolean = true
    ): Dialog {
        val dialogRate = Dialog(applicationContext!!)
        dialogRate.setContentView(dialog_update_version)
        dialogRate.setCanceledOnTouchOutside(isCanceledOnTouchOutside)

        dialogRate.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialogRate.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialogRate
    }

    fun onCreateDialogMatchParent(
        applicationContext: Context?,
        dialog_update_version: Int,
        isCanceledOnTouchOutside: Boolean = true
    ): Dialog {
        val dialogRate = Dialog(applicationContext!!)
        dialogRate.setContentView(dialog_update_version)
        dialogRate.setCanceledOnTouchOutside(isCanceledOnTouchOutside)

        dialogRate.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialogRate.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialogRate
    }

    private fun getLauncherTopApp(context: Context): String {
        val sUsageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        //isLockTypeAccessibility = SpUtil.getInstance().getBoolean(Constants.LOCK_TYPE, false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            val appTasks = activityManager.getRunningTasks(1)
            if (null != appTasks && !appTasks.isEmpty()) {
                return appTasks[0].topActivity!!.packageName
            }
        } else {
            val endTime = System.currentTimeMillis()
            val beginTime = endTime - 10000
            var result = ""
            val event = UsageEvents.Event()
            val usageEvents: UsageEvents = sUsageStatsManager.queryEvents(beginTime, endTime)
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    result = event.packageName
                }
            }
            if (!TextUtils.isEmpty(result)) {
                return result
            }
        }
        return ""
    }

    fun isSeenTutorial(context: Context): Boolean {
        val sharePef = context.getSharedPreferences(Constants.APP_NAME, Activity.MODE_PRIVATE)
        return sharePef.getBoolean("tutorial", false)
    }

    fun setSeenTutorial(context: Context) {
        val sharePef = context.getSharedPreferences(Constants.APP_NAME, Activity.MODE_PRIVATE)
        val editor = sharePef.edit()
        editor.putBoolean("tutorial", true)
        editor.apply()
    }

    fun isGetLanguage(context: Context): Boolean {
        val sharePef = context.getSharedPreferences(Constants.APP_NAME, Activity.MODE_PRIVATE)
        return sharePef.getBoolean(Constants.IS_SET_LANG, false)
    }

    fun isSetLanguage(context: Context) {
        val sharePef = context.getSharedPreferences(Constants.APP_NAME, Activity.MODE_PRIVATE)
        val editor = sharePef.edit()
        editor.putBoolean(Constants.IS_SET_LANG, true)
        editor.apply()
    }

    fun isFirstMeasure(context: Context): Boolean {
        val sharePef = context.getSharedPreferences(Constants.APP_NAME, Activity.MODE_PRIVATE)
        return sharePef.getBoolean(Constants.IS_FIRST_MEASURE, false)
    }

    fun setFirstMeasure(context: Context) {
        val sharePef = context.getSharedPreferences(Constants.APP_NAME, Activity.MODE_PRIVATE)
        val editor = sharePef.edit()
        editor.putBoolean(Constants.IS_FIRST_MEASURE, true)
        editor.apply()
    }

    fun getIOSCountryData(context: Context): String {
        val sharePef = context.getSharedPreferences(Constants.APP_NAME, Activity.MODE_PRIVATE)
        return sharePef.getString(Constants.KEY_LANG, "en").toString()
    }

    fun setIOSCountryData(lang: String, context: Context) {
        val sharePef = context.getSharedPreferences(Constants.APP_NAME, Activity.MODE_PRIVATE)
        val editor = sharePef.edit()
        editor.putString(Constants.KEY_LANG, lang)
        editor.apply()
    }

    fun getCurrentFlag(context: Context): Int {
        val sharePef = context.getSharedPreferences(Constants.APP_NAME, Activity.MODE_PRIVATE)
        return sharePef.getInt(Constants.KEY_FLAG, 0)
    }

    fun setCurrentFlag(flag: Int, context: Context) {
        val sharePef = context.getSharedPreferences(Constants.APP_NAME, Activity.MODE_PRIVATE)
        val editor = sharePef.edit()
        editor.putInt(Constants.KEY_FLAG, flag)
        editor.apply()
    }

    fun setLanguageForApp(context: Context) {
        val languageToLoad = getIOSCountryData(context)
        val locale: Locale = if (languageToLoad == "not-set") {
            Locale.getDefault()
        } else {
            Locale(languageToLoad)
        }
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        context.resources.updateConfiguration(
            config,
            context.resources.displayMetrics
        )
    }


    fun changeStatusBarColor(activity: Activity, color: Int) {
        val window = activity.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(activity, color)
        }
    }

    fun calculateBMI(weight: Float, height: Float): Float {
        return (((weight / height) / height) * 10000).roundToInt().toFloat()
    }

    fun isEnglishLanguage(): Boolean {
        return Locale.getDefault().language == "en"
    }
}