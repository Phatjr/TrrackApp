package com.phat.trackerapp.screen.bmi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.phat.trackerapp.R
import com.phat.trackerapp.callback.OnItemListener
import com.phat.trackerapp.data.model.BMI
import com.phat.trackerapp.data.model.BMIType
import com.phat.trackerapp.utils.Utils
import kotlinx.android.synthetic.main.layout_item_bmi.view.ivEdit
import kotlinx.android.synthetic.main.layout_item_bmi.view.lineState
import kotlinx.android.synthetic.main.layout_item_bmi.view.tvBMI
import kotlinx.android.synthetic.main.layout_item_bmi.view.tvHeight
import kotlinx.android.synthetic.main.layout_item_bmi.view.tvState
import kotlinx.android.synthetic.main.layout_item_bmi.view.tvTag
import kotlinx.android.synthetic.main.layout_item_bmi.view.tvTime
import kotlinx.android.synthetic.main.layout_item_bmi.view.tvWeight

class BMIAdapter(
    var context: Context,
    var mList: ArrayList<BMI>,
    var onItemClickListener: OnItemListener
) :
    RecyclerView.Adapter<BMIAdapter.MyViewHolder>() {
    var mBmiTypes: ArrayList<BMIType> = BMIType.getAllType()


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_bmi, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemView = holder.itemView
        val bmi = mList[position]

        itemView.apply {
//            val time = android.text.format.DateFormat.format(
//                "MMM dd, yyyy HH:mm",
//                Date(bmi.datetime)
//            )
            tvTime.text = bmi.time +", "+bmi.date
            if (bmi.tag.isNullOrEmpty()) {
                tvTag.visibility = View.GONE
            } else {
                tvTag.visibility = View.VISIBLE
            }

            tvTag.text = "#${bmi.tag}"

            tvWeight.text = bmi.weight.toString()
            val bmiValue = Utils.calculateBMI(bmi.weight, bmi.height)

            val pos = BMIType.getPositionType(bmiValue)

            val colorState = ContextCompat.getColor(context, mBmiTypes[pos].color)

            lineState.setBackgroundColor(colorState)

            tvState.setText(mBmiTypes[pos].state)

            tvBMI.setText("(BMI ${String.format("%.1f", bmiValue)})")

            tvHeight.setText("Height: ${bmi.height} cm")

            ivEdit.setOnClickListener {
                onItemClickListener.onItemClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

}