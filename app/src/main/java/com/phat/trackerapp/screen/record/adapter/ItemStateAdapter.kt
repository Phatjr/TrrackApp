package com.phat.trackerapp.screen.record.adapter

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.phat.trackerapp.R
import com.phat.trackerapp.callback.OnItemListener
import kotlinx.android.synthetic.main.activity_edit_add_blood_sugar.view.tvState
import kotlinx.android.synthetic.main.layout_item_state_blood_sugar.view.itemBg
import kotlinx.android.synthetic.main.layout_item_state_blood_sugar.view.rbCheck

class ItemStateAdapter(
    var context: Context,
    var mStates: ArrayList<Int>,
    var onItemClickListener: OnItemListener
) :
    RecyclerView.Adapter<ItemStateAdapter.MyViewHolder>() {
    var mPosSelected: Int = 0
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_state_blood_sugar, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemView = holder.itemView
        val state = mStates[position]

        itemView.apply {
           tvState.setText(state)
            rbCheck.isChecked = position == mPosSelected
            if(position == mPosSelected){
                itemBg.background.setColorFilter(ContextCompat.getColor(context,R.color.white),PorterDuff.Mode.SRC_IN)
            }else{
                itemBg.background.setColorFilter(ContextCompat.getColor(context,R.color.color_bg_main),PorterDuff.Mode.SRC_IN)
            }

            setOnClickListener {
                onItemClickListener.onItemClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return mStates.size
    }

}