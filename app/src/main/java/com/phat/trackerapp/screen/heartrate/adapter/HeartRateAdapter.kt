package com.phat.trackerapp.screen.heartrate.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.phat.trackerapp.R
import com.phat.trackerapp.callback.OnItemListener
import com.phat.trackerapp.utils.measuring.model.HeartRate
import kotlinx.android.synthetic.main.layout_item_heart_rate.view.btnEdit
import kotlinx.android.synthetic.main.layout_item_heart_rate.view.lineState
import kotlinx.android.synthetic.main.layout_item_heart_rate.view.tvHeartRate
import kotlinx.android.synthetic.main.layout_item_heart_rate.view.tvState
import kotlinx.android.synthetic.main.layout_item_heart_rate.view.tvTag
import kotlinx.android.synthetic.main.layout_item_heart_rate.view.tvTime

class HeartRateAdapter(
    var context: Context,
    var mList: ArrayList<HeartRate>,
    var onItemClickListener: OnItemListener
) :
    RecyclerView.Adapter<HeartRateAdapter.MyViewHolder>() {


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_heart_rate, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemView = holder.itemView
        val heartRate = mList[position]

        itemView.apply {
            tvTime.text = heartRate.time +", "+heartRate.date
            if (heartRate.tag.isEmpty()) {
                tvTag.visibility = View.GONE
            } else {
                tvTag.visibility = View.VISIBLE
            }
            tvTag.text = heartRate.tag

            tvHeartRate.text = heartRate.heartRate.toString()
            val value = heartRate.heartRate
            if (value in 40..59) {
                tvState.setText(R.string.txt_slow)
                lineState.setBackgroundColor(ContextCompat.getColor(context, R.color.purple))
            } else if (value in 60..100) {
                tvState.setText(R.string.txt_normal)
                lineState.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
            } else {
                tvState.setText(R.string.txt_fast)
                lineState.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
            }

            btnEdit.setOnClickListener {
                onItemClickListener.onItemClick(position)
            }

        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

}