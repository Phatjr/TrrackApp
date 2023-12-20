package com.phat.trackerapp.screen.home.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.phat.trackerapp.R
import com.phat.trackerapp.callback.OnItemListener
import com.phat.trackerapp.screen.home.model.InfoKnowledge
import kotlinx.android.synthetic.main.layout_item_info.view.imgInfo
import kotlinx.android.synthetic.main.layout_item_info.view.imgNext
import kotlinx.android.synthetic.main.layout_item_info.view.layoutInfo
import kotlinx.android.synthetic.main.layout_item_info.view.txtTitle


class InfoKnowledgeAdapter(
    var context: Context,
    var infoKnowledges: ArrayList<InfoKnowledge>,
    var onItemListener: OnItemListener
) : RecyclerView.Adapter<InfoKnowledgeAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.layout_item_info, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemView = holder.itemView
        val infoKnowledge = infoKnowledges[position]


        //todo Glide là thư viện dùng để load ảnh vào imageview
        Glide.with(context).asDrawable().load(infoKnowledge.idBg)
            .into(object : SimpleTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: com.bumptech.glide.request.transition.Transition<in Drawable>?
                ) {
                    itemView.layoutInfo.background = resource
                }
            })

        Glide.with(context).load(infoKnowledge.idIconInfo).into(itemView.imgInfo)

        Glide.with(context).load(infoKnowledge.idImageNext).into(itemView.imgNext)

        itemView.txtTitle.text = context.getString(infoKnowledge.idTitle)

        itemView.setOnClickListener {
            onItemListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return infoKnowledges.size
    }
}