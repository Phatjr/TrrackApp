package com.phat.trackerapp.screen.home.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.phat.trackerapp.R
import com.phat.trackerapp.callback.OnItemListener
import com.phat.trackerapp.data.model.TimeAlarm
import kotlinx.android.synthetic.main.layout_item_alarm_reminder.view.img2
import kotlinx.android.synthetic.main.layout_item_alarm_reminder.view.img3
import kotlinx.android.synthetic.main.layout_item_alarm_reminder.view.img4
import kotlinx.android.synthetic.main.layout_item_alarm_reminder.view.img5
import kotlinx.android.synthetic.main.layout_item_alarm_reminder.view.img6
import kotlinx.android.synthetic.main.layout_item_alarm_reminder.view.img7
import kotlinx.android.synthetic.main.layout_item_alarm_reminder.view.imgCN
import kotlinx.android.synthetic.main.layout_item_alarm_reminder.view.lineType
import kotlinx.android.synthetic.main.layout_item_alarm_reminder.view.tvTypeName
import kotlinx.android.synthetic.main.layout_item_alarm_reminder.view.txtTime

class TimeAlarmAdapter(
    var context: Context,
    var timeAlarms: ArrayList<TimeAlarm>,
    var onItemListener: OnItemListener
) : RecyclerView.Adapter<TimeAlarmAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(context)
                .inflate(R.layout.layout_item_alarm_reminder, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val itemView = holder.itemView

        val timeAlarm = timeAlarms[position]
        Log.d("9999999time$position=",""+timeAlarm.day1+"_"+timeAlarm.day2+"_"+timeAlarm.day3+"_"+timeAlarm.day4+"_"+timeAlarm.day5+"_"+timeAlarm.day6+"_"+timeAlarm.day7)


        val hour = if(timeAlarm.hour<10) {
            "0${timeAlarm.hour}"
        }else{
            "${timeAlarm.hour}"
        }

        val minute = if(timeAlarm.minute<10) {
            "0${timeAlarm.minute}"
        }else{
            "${timeAlarm.minute}"
        }
        itemView.txtTime.text = "${hour}:${minute}"
        itemView.apply {
            when(timeAlarm.typeAlarm){
                0 ->{
                    lineType.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    tvTypeName.setText(R.string.txt_blood_pressure)
                }
                1 ->{
                    lineType.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
                    tvTypeName.setText(R.string.txt_heart_rate)
                }
                2 ->{
                    lineType.setBackgroundColor(ContextCompat.getColor(context, R.color.color_blood_sugar))
                    tvTypeName.setText(R.string.txt_blood_sugar)
                }
                else ->{
                    lineType.setBackgroundColor(ContextCompat.getColor(context, R.color.color_weight_and_bmi))
                    tvTypeName.setText(R.string.txt_weight_and_bmi)
                }
            }
        }


        if(timeAlarm.day1) {
            itemView.img2.setImageResource(R.drawable.ic_tick_alarm)
        }else{
            itemView.img2.setImageResource(R.drawable.ic_tick_alarm_diable)
        }

        if(timeAlarm.day2) {
            itemView.img3.setImageResource(R.drawable.ic_tick_alarm)
        }else{
            itemView.img3.setImageResource(R.drawable.ic_tick_alarm_diable)
        }

        if(timeAlarm.day3) {
            itemView.img4.setImageResource(R.drawable.ic_tick_alarm)
        }else{
            itemView.img4.setImageResource(R.drawable.ic_tick_alarm_diable)
        }

        if(timeAlarm.day4) {
            itemView.img5.setImageResource(R.drawable.ic_tick_alarm)
        }else{
            itemView.img5.setImageResource(R.drawable.ic_tick_alarm_diable)
        }

        if(timeAlarm.day5) {
            itemView.img6.setImageResource(R.drawable.ic_tick_alarm)
        }else{
            itemView.img6.setImageResource(R.drawable.ic_tick_alarm_diable)
        }

        if(timeAlarm.day6) {
            itemView.img7.setImageResource(R.drawable.ic_tick_alarm)
        }else{
            itemView.img7.setImageResource(R.drawable.ic_tick_alarm_diable)
        }

        if(timeAlarm.day7) {
            itemView.imgCN.setImageResource(R.drawable.ic_tick_alarm)
        }else{
            itemView.imgCN.setImageResource(R.drawable.ic_tick_alarm_diable)
        }

        itemView.setOnClickListener {
            onItemListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return timeAlarms.size
    }


}