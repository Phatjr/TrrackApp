package com.phat.trackerapp.screen.record.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phat.trackerapp.R
import com.phat.trackerapp.database.SharePrefDB
import kotlinx.android.synthetic.main.layout_item_tag_note.view.layoutTag
import kotlinx.android.synthetic.main.layout_item_tag_note.view.txtContentTag

class TagNoteAdapter(var context: Context, var tags: ArrayList<String>): RecyclerView.Adapter<TagNoteAdapter.MyViewHolder>() {

    var tagSelected: ArrayList<String> = ArrayList()

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_tag_note, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       val itemView = holder.itemView
        val tag = tags[position]
        itemView.txtContentTag.text = tag
        if(tagSelected.contains(tag)) {
            itemView.layoutTag.setBackgroundResource(R.drawable.bg_select_note)
        }else{
            itemView.layoutTag.setBackgroundResource(R.drawable.buttons_ripple_note)
        }

        itemView.setOnClickListener {
            if(tagSelected.contains(tag)) {
                tagSelected.remove(tag)
            }else{
                tagSelected.add(tag)
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return tags.size
    }

    fun reloadTag(key: String) {
        tags.clear()
        tags.addAll(SharePrefDB.getInstance(context).getAllNotes(key).toList())
        notifyDataSetChanged()
    }

    fun getAllTagSelected():String {
        var tag = ""
        for(i in 0 until tagSelected.size) {
            tag = tag+ "#"+tagSelected[i]
        }
        return tag
    }
}