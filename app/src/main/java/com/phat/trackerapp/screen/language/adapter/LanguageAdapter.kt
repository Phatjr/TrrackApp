package com.phat.trackerapp.screen.language.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phat.trackerapp.R
import com.phat.trackerapp.screen.language.activity.LanguageActivity
import com.phat.trackerapp.screen.language.model.Country
import kotlinx.android.synthetic.main.layout_item_language.view.imgIconFlag
import kotlinx.android.synthetic.main.layout_item_language.view.rbCheck
import kotlinx.android.synthetic.main.layout_item_language.view.txtNameCountry

class LanguageAdapter(var context: Context, var countries: ArrayList<Country>, var onItemLanguageListener: OnItemLanguageListener):RecyclerView.Adapter<LanguageAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val myView = LayoutInflater.from(context).inflate(R.layout.layout_item_language, parent, false)
        return MyViewHolder(myView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemView = holder.itemView
        val country = countries[position]

        itemView.imgIconFlag.setImageResource(country.icon)

        itemView.txtNameCountry.text = country.name

        if(context is LanguageActivity){
            itemView.rbCheck.isChecked = (context as LanguageActivity).mPosCheck == position
        }


        itemView.setOnClickListener {
            onItemLanguageListener.onItemLanguageClick(position)
        }

        itemView.rbCheck.setOnClickListener {
            onItemLanguageListener.onItemLanguageClick(position)
        }
    }

    override fun getItemCount(): Int {
        return countries.size
    }

    interface OnItemLanguageListener {
        fun onItemLanguageClick(position: Int)
    }
}