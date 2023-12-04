package com.phat.trackerapp.screen.info.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phat.trackerapp.R
import kotlinx.android.synthetic.main.layout_item_detail_info.view.tvContentInfo


class InfoAdapter(var context: Context, var mInfos: ArrayList<String>):RecyclerView.Adapter<InfoAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.layout_item_detail_info, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemView = holder.itemView
        val info = mInfos[position]

        itemView.tvContentInfo.text = info

    }

    override fun getItemCount(): Int {
        return mInfos.size
    }
}