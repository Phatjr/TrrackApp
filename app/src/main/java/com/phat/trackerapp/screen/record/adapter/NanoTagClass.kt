package com.phat.trackerapp.screen.record.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class NanoTagClass(context: Context, recyclerView: RecyclerView, val chipAdapter: TagNoteAdapter) {

    val allChipsValues: ArrayList<String>
        get() {
            val arrList = ArrayList<String>()
            for (chipValue in chipAdapter.tags) {
                arrList.add(chipValue)
            }
            return arrList
        }

    init {
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = chipAdapter
    }
}