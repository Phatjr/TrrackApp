package com.phat.trackerapp.screen.note.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phat.trackerapp.R
import com.phat.trackerapp.callback.OnItemListener
import com.phat.trackerapp.screen.note.callback.ItemMoveCallback
import kotlinx.android.synthetic.main.layout_item_drag_item_tag.view.btnDelete
import kotlinx.android.synthetic.main.layout_item_drag_item_tag.view.txtNote
import java.util.Collections

class ChipNoteAdapter(
    var context: Context,
    var chips: ArrayList<String>,
    var onItemListener: OnItemListener
) : RecyclerView.Adapter<ChipNoteAdapter.MyViewHolder>(), ItemMoveCallback.ItemTouchHelperContract {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_drag_item_tag, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val itemView = holder.itemView
        val chip = chips[position]
        itemView.txtNote.text = chip

        itemView.setOnClickListener {

        }

        itemView.btnDelete.setOnClickListener {
            onItemListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return chips.size
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(chips, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(chips, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    fun addNote(note: String) {
        if (chips.contains(note)) {
            chips.remove(note)
        }
        chips.add(0, note)
        notifyDataSetChanged()
    }

    fun removeNote(pos: Int) {
        chips.removeAt(pos)
        notifyDataSetChanged()
    }
}