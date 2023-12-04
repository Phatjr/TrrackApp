package com.phat.trackerapp.screen.home.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.phat.trackerapp.screen.home.fragment.HomeFragment
import com.phat.trackerapp.screen.home.fragment.InfoFragment
import com.phat.trackerapp.screen.home.fragment.SettingsFragment

class MyViewPaperAdapter(fragmentActivity: FragmentActivity):FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> HomeFragment.newInstance()
            1 -> InfoFragment.newInstance()
            else -> SettingsFragment.newInstance()
        }
    }

}