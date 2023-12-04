package com.phat.trackerapp.screen.note.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class NanoChipClass(context: Context, recyclerView: RecyclerView, val chipAdapter: ChipNoteAdapter) {


    val allChipsValues: ArrayList<String>
        get() {
            val arrList = ArrayList<String>()
            for (chipValue in chipAdapter.chips) {
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