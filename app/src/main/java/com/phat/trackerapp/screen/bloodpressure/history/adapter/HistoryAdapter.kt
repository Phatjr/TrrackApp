package com.phat.trackerapp.screen.bloodpressure.history.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.phat.trackerapp.R
import com.phat.trackerapp.callback.OnItemListener
import com.phat.trackerapp.data.model.Tracker
import kotlinx.android.synthetic.main.layout_item_history.view.btnEdit
import kotlinx.android.synthetic.main.layout_item_history.view.txtDiastolic
import kotlinx.android.synthetic.main.layout_item_history.view.txtPulse
import kotlinx.android.synthetic.main.layout_item_history.view.txtState
import kotlinx.android.synthetic.main.layout_item_history.view.txtSystolic
import kotlinx.android.synthetic.main.layout_item_history.view.txtTag
import kotlinx.android.synthetic.main.layout_item_history.view.txtTimeDate
import kotlinx.android.synthetic.main.layout_item_history.view.viewState

class HistoryAdapter(
    var context: Context,
    var trackers: ArrayList<Tracker>,
    var onItemListener: OnItemListener
) : RecyclerView.Adapter<HistoryAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_history, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemView = holder.itemView
        val tracker = trackers[position]
        val valueSystolic = tracker.systolic
        val valueDiastolic = tracker.diastolic

        itemView.txtSystolic.text = valueSystolic.toString()
        itemView.txtDiastolic.text = tracker.diastolic.toString()
        itemView.txtPulse.text =
            context.getString(R.string.txt_pulse) + ": " + tracker.pulse.toString()
        itemView.txtTimeDate.text = tracker.time + ", " + tracker.date
        itemView.txtState.text = tracker.state
        itemView.txtTag.text = tracker.tag

        if (valueSystolic in 0..119) {
            if (valueDiastolic in 0..79) {
                itemView.viewState.setCardBackgroundColor(Color.parseColor("#00C57E"))
            }
        }

        if (valueSystolic in 120..129) {
            if (valueDiastolic in 0..79) {
                itemView.viewState.setCardBackgroundColor(Color.parseColor("#E9D841"))
            }
        }

        val z2 = valueSystolic in 130..139
        if (z2 || valueDiastolic in 80..89) {
            itemView.viewState.setCardBackgroundColor(Color.parseColor("#F7B11E"))
        }

        val z3 = valueSystolic in 140..180
        if (z3 || valueDiastolic in 90..120) {
            itemView.viewState.setCardBackgroundColor(Color.parseColor("#FF6B00"))
        }

        if (valueSystolic > 180 || valueDiastolic > 120) {
            itemView.viewState.setCardBackgroundColor(Color.parseColor("#D72626"))
        }

        itemView.btnEdit.setOnClickListener {
            onItemListener.onItemClick(position)
            Toast.makeText(context, "$position", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return trackers.size
    }
}