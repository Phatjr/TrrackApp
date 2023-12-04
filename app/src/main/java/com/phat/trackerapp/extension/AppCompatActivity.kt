package com.phat.trackerapp.extension

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.horen.chart.barchart.BarChartHelper
import com.horen.chart.barchart.IBarData
import com.phat.trackerapp.R
import java.io.File
import java.util.*


fun AppCompatActivity.showNotice(msg: String){
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun EditText.textString(): String{
    return text.toString().trim()
}

fun EditText.isDecimal(): Boolean{
    return textString().toFloat() >= 1
}

fun AppCompatActivity.onCreateBarChart(data: ArrayList<List<IBarData>>, xDays: ArrayList<String>, chart: BarChart){
    val colors = resources.getStringArray(com.horen.chart.R.array.chart_colors)
    val chartColors: MutableList<Int> = ArrayList()
    for (color in colors) {
        chartColors.add(Color.parseColor(color))
    }

    BarChartHelper.Builder()
        .setContext(this)
        .setBarChart(chart)
        .setBarSetData(data)
        .setLabels(xDays)
        .setDisplayCount(2)
        .setLegendEnable(true)
        .setLegendTextSize(14f)
        .setyAxisRightEnable(false)
        .setDrawGridLines(true)
        .setScaleEnabled(true)
        .setDoubleTapToZoomEnabled(true)
        .setDescriptionEnable(false)
        .setPinchZoom(true)
        .setGroupSpace(0.4f)
        .setDurationMillis(2000)
        .setEasing(Easing.Linear)
        .setBarColors(chartColors)
        .setXValueEnable(true)
        .build()
}

fun AppCompatActivity.onCreateBarChartOneColumn(data: List<IBarData>, xDays: ArrayList<String>, chart: BarChart){
    BarChartHelper.Builder()
        .setContext(this)
        .setBarChart(chart)
        .setBarData(data)
        .setLabels(xDays)
        .setDisplayCount(4)
        .setLegendTextSize(14f)
        .setLegendEnable(true)
        .setyAxisRightEnable(false)
        .setDrawGridLines(true)
        .setScaleEnabled(true)
        .setDoubleTapToZoomEnabled(true)
        .setDescriptionEnable(false)
        .setPinchZoom(true)
        .setDurationMillis(2000)
        .setEasing(Easing.Linear)
        .setBarColor(ContextCompat.getColor(this, R.color.color_yellow))
        .setXValueEnable(true)
        .build()
}

fun AppCompatActivity.showDialogChooseDate(txtDate: TextView) {
    val c = Calendar.getInstance()
    val yearCurrent = c.get(Calendar.YEAR)
    val monthCurrent = c.get(Calendar.MONTH)
    val dayCurrent = c.get(Calendar.DAY_OF_MONTH)
    val dpd = DatePickerDialog(this, { view, year, monthOfYear, dayOfMonth ->
        txtDate.text = "$dayOfMonth/${monthOfYear + 1}/$year"
    }, yearCurrent, monthCurrent, dayCurrent)
    dpd.show()
}

fun Int.convertTwoDigits(): String {
    return if (this < 10) "0$this" else "$this"
}

fun AppCompatActivity.showDialogChooseTime(txtTime: TextView) {
    val currentTime = Calendar.getInstance()
    val hour = currentTime.get(Calendar.HOUR_OF_DAY)
    val minute = currentTime.get(Calendar.MINUTE)

    TimePickerDialog(
        this,
        { view, hourOfDay, minute ->
            txtTime.text = "${hourOfDay.convertTwoDigits()}:$minute"
        }, hour, minute, true
    ).show()
}

fun AppCompatActivity.getDateFormat(time: Long): String {
    return android.text.format.DateFormat.format(
        "HH:MM, dd/MM/yyyy",
        Date(time)
    ).toString()
}

fun AppCompatActivity.getTimeFormat(time: Long): String {
    return android.text.format.DateFormat.format(
        "HH:mm",
        Date(time)
    ).toString()
}

fun AppCompatActivity.shareCsvFile(path: String){
    try {
        val file = File(path)
        if (file.exists()) {
            val uri = FileProvider.getUriForFile(
                this,
                com.phat.trackerapp.BuildConfig.APPLICATION_ID + ".provider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.type = "text/csv"
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}