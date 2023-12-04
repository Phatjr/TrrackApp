package com.phat.trackerapp.screen.info

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.phat.trackerapp.R
import com.phat.trackerapp.screen.info.adapter.InfoAdapter
import com.phat.trackerapp.utils.Constants
import kotlinx.android.synthetic.main.activity_info.btnBack
import kotlinx.android.synthetic.main.activity_info.imgIcon
import kotlinx.android.synthetic.main.activity_info.rlBackground
import kotlinx.android.synthetic.main.activity_info.rvInfo
import kotlinx.android.synthetic.main.activity_info.tvTitle

class InfoActivity : AppCompatActivity() {
    private var mID: Int = 0

    private lateinit var mInfoAdapter: InfoAdapter

    private lateinit var mInfos: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        initData()
        handleEvents()
    }

    private fun initData() {
        mID = intent.getIntExtra(Constants.ID, R.array.info_des_array_1)

        val idIcon = intent.getIntExtra(Constants.ICON,R.drawable.ic_info_1)
        Glide.with(this).load(idIcon).into(imgIcon)
        tvTitle.text = getString(intent.getIntExtra(Constants.TITLE, R.string.txt_title_info_1))

        val idBg = intent.getIntExtra(Constants.BACKGROUND,R.drawable.ic_bg_info_1)

        Glide.with(this).asDrawable().load(idBg)
            .into(object : SimpleTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: com.bumptech.glide.request.transition.Transition<in Drawable>?
                ) {
                    rlBackground.background = resource
                }
            })

        mInfos = ArrayList()
        mInfos.addAll(resources.getStringArray(mID))
        rvInfo.layoutManager = LinearLayoutManager(this)
        mInfoAdapter = InfoAdapter(this,mInfos)
        rvInfo.adapter = mInfoAdapter
    }

    private fun handleEvents() {
        btnBack.setOnClickListener {
            finish()
        }


    }
}