package com.phat.trackerapp.screen.home.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.phat.trackerapp.R
import com.phat.trackerapp.callback.OnItemListener
import com.phat.trackerapp.screen.home.HomeActivity
import com.phat.trackerapp.screen.home.adapter.InfoKnowledgeAdapter
import com.phat.trackerapp.screen.home.model.InfoKnowledge
import com.phat.trackerapp.screen.info.InfoActivity
import com.phat.trackerapp.utils.Constants
import kotlinx.android.synthetic.main.fragment_info.rvInfoKnowledge

class InfoFragment : Fragment(), OnItemListener {
    private lateinit var infoKnowledges: ArrayList<InfoKnowledge>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        handleEvents()
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(requireContext(), InfoActivity::class.java)
        intent.putExtra(Constants.ICON, infoKnowledges[position].idIconInfo)
        intent.putExtra(Constants.TITLE, infoKnowledges[position].idTitle)
        intent.putExtra(Constants.BACKGROUND, infoKnowledges[position].idBg)
        intent.putExtra(Constants.ID, infoKnowledges[position].idDesArray)

        if (context is HomeActivity) {
            (context as HomeActivity).showScreen(intent)
        }
    }

    private fun initData() {
        infoKnowledges = ArrayList()
        infoKnowledges.add(
            InfoKnowledge(
                R.drawable.ic_bg_info_1,
                R.drawable.ic_info_1,
                R.drawable.ic_next,
                R.string.txt_title_info_1,
                R.array.info_des_array_1
            )
        )
        infoKnowledges.add(
            InfoKnowledge(
                R.drawable.ic_bg_info_2,
                R.drawable.ic_info_2,
                R.drawable.ic_next,
                R.string.txt_title_info_2,
                R.array.info_des_array_2
            )
        )
        infoKnowledges.add(
            InfoKnowledge(
                R.drawable.ic_bg_info_3,
                R.drawable.ic_info_3,
                R.drawable.ic_next,
                R.string.txt_title_info_3,
                R.array.info_des_array_3
            )
        )
        infoKnowledges.add(
            InfoKnowledge(
                R.drawable.ic_bg_info_4,
                R.drawable.ic_info_4,
                R.drawable.ic_next,
                R.string.txt_title_info_4,
                R.array.info_des_array_4
            )
        )
        infoKnowledges.add(
            InfoKnowledge(
                R.drawable.ic_bg_info_5,
                R.drawable.ic_info_5,
                R.drawable.ic_next,
                R.string.txt_title_info_5,
                R.array.info_des_array_5
            )
        )
        infoKnowledges.add(
            InfoKnowledge(
                R.drawable.ic_bg_info_6,
                R.drawable.ic_info_6,
                R.drawable.ic_next,
                R.string.txt_title_info_6,
                R.array.info_des_array_6
            )
        )
        infoKnowledges.add(
            InfoKnowledge(
                R.drawable.ic_bg_info_7,
                R.drawable.ic_info_7,
                R.drawable.ic_next,
                R.string.txt_title_info_7,
                R.array.info_des_array_7
            )
        )
        infoKnowledges.add(
            InfoKnowledge(
                R.drawable.ic_bg_info_8,
                R.drawable.ic_info_8,
                R.drawable.ic_next,
                R.string.txt_title_info_8,
                R.array.info_des_array_8
            )
        )
        infoKnowledges.add(
            InfoKnowledge(
                R.drawable.ic_bg_info_9,
                R.drawable.ic_info_9,
                R.drawable.ic_next,
                R.string.txt_title_info_9,
                R.array.info_des_array_9
            )
        )
        infoKnowledges.add(
            InfoKnowledge(
                R.drawable.ic_bg_info_10,
                R.drawable.ic_info_10,
                R.drawable.ic_next,
                R.string.txt_title_info_10,
                R.array.info_des_array_10
            )
        )
        infoKnowledges.add(
            InfoKnowledge(
                R.drawable.ic_bg_info_11,
                R.drawable.ic_info_11,
                R.drawable.ic_next,
                R.string.txt_title_info_11,
                R.array.info_des_array_11
            )
        )
        infoKnowledges.add(
            InfoKnowledge(
                R.drawable.ic_bg_info_12,
                R.drawable.ic_info_12,
                R.drawable.ic_next,
                R.string.txt_title_info_12,
                R.array.info_des_array_12
            )
        )
        infoKnowledges.add(
            InfoKnowledge(
                R.drawable.ic_bg_info_13,
                R.drawable.ic_info_13,
                R.drawable.ic_next,
                R.string.txt_title_info_13,
                R.array.info_des_array_13
            )
        )
        infoKnowledges.add(
            InfoKnowledge(
                R.drawable.ic_bg_info_14,
                R.drawable.ic_info_14,
                R.drawable.ic_next,
                R.string.txt_title_info_14,
                R.array.info_des_array_14
            )
        )
        infoKnowledges.add(
            InfoKnowledge(
                R.drawable.ic_bg_info_15,
                R.drawable.ic_info_15,
                R.drawable.ic_next,
                R.string.txt_title_info_15,
                R.array.info_des_array_15
            )
        )
        infoKnowledges.add(
            InfoKnowledge(
                R.drawable.ic_bg_info_16,
                R.drawable.ic_info_16,
                R.drawable.ic_next,
                R.string.txt_title_info_16,
                R.array.info_des_array_16
            )
        )

        val infoKnowledgeAdapter = InfoKnowledgeAdapter(requireContext(), infoKnowledges, this)
        rvInfoKnowledge.layoutManager = GridLayoutManager(requireContext(), 2)
        rvInfoKnowledge.adapter = infoKnowledgeAdapter
    }

    private fun handleEvents() {

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            InfoFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}