package com.phat.trackerapp.screen.bloodsugar.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.phat.trackerapp.R
import com.phat.trackerapp.callback.OnItemListener
import com.phat.trackerapp.data.model.BloodSugar
import kotlinx.android.synthetic.main.layout_item_blood_sugar.view.ivEdit
import kotlinx.android.synthetic.main.layout_item_blood_sugar.view.lineState
import kotlinx.android.synthetic.main.layout_item_blood_sugar.view.tvState
import kotlinx.android.synthetic.main.layout_item_blood_sugar.view.tvTag
import kotlinx.android.synthetic.main.layout_item_blood_sugar.view.tvTime
import kotlinx.android.synthetic.main.layout_item_blood_sugar.view.tvTypeState
import kotlinx.android.synthetic.main.layout_item_blood_sugar.view.tvUnit
import kotlinx.android.synthetic.main.layout_item_blood_sugar.view.tvValue

class BloodSugarAdapter(
    var context: Context,
    var mList: ArrayList<BloodSugar>,
    var onItemClickListener: OnItemListener
) :
    RecyclerView.Adapter<BloodSugarAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_blood_sugar, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemView = holder.itemView
        val bloodSugar = mList[position]

        itemView.apply {
//            val time = android.text.format.DateFormat.format(
//                "MMM dd, yyyy HH:mm",
//                Date(bloodSugar.datetime)
//            )
            tvTime.text = bloodSugar.time + ", "+ bloodSugar.date
            if (bloodSugar.tag.isNullOrEmpty()) {
                tvTag.visibility = View.GONE
            } else {
                tvTag.visibility = View.VISIBLE
            }

            tvTag.text = bloodSugar.tag

            Log.d("444444",context.getString(BloodSugar.getTypeState(bloodSugar.typeState)))

            tvTypeState.text = "${context.getString(R.string.txt_state)}: ${context.getString(bloodSugar.typeState)}"

            tvValue.setText(String.format("%.1f", bloodSugar.value))
            if(bloodSugar.unit == 18f){
                tvUnit.text =  "mg/dL"
            }else{
                tvUnit.text =  "mmol/l"
            }
            when(bloodSugar.state){
                0 ->{
                    tvState.setText(R.string.txt_low)
                    lineState.setBackgroundColor(ContextCompat.getColor(context,R.color.color_bmi_type_1))
                }
                1 ->{
                    tvState.setText(R.string.txt_normal)
                    lineState.setBackgroundColor(ContextCompat.getColor(context,R.color.color_bmi_type_2))
                }
                2 ->{
                    tvState.setText(R.string.txt_pre_diabetes)
                    lineState.setBackgroundColor(ContextCompat.getColor(context,R.color.color_bmi_type_4))
                }
                3 ->{
                    tvState.setText(R.string.txt_diabetes)
                    lineState.setBackgroundColor(ContextCompat.getColor(context,R.color.color_bmi_type_5))
                }
            }

            ivEdit.setOnClickListener {
                onItemClickListener.onItemClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

}