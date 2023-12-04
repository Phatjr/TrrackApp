package com.phat.trackerapp.screen.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.phat.trackerapp.R
import com.phat.trackerapp.extension.showNotice
import com.phat.trackerapp.screen.home.fragment.HomeFragment
import com.phat.trackerapp.screen.home.fragment.InfoFragment
import com.phat.trackerapp.screen.home.fragment.SettingsFragment
import com.phat.trackerapp.screen.language.activity.LanguageActivity
import com.phat.trackerapp.utils.Utils
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    private var mExitTime: Long = 0

    private var mIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setLanguageForApp(this)
        setContentView(R.layout.activity_home)
        initView()
        handleEvent()
    }

    override fun onResume() {
        super.onResume()
        val flag = if (Utils.getCurrentFlag(this) == 0) {
            R.drawable.ic_flag_english
        } else {
            Utils.getCurrentFlag(this)
        }
        Glide.with(this)
            .load(flag).error(R.drawable.ic_flag_english)
            .placeholder(R.drawable.ic_flag_english)
            .into(imgFlag)
    }

    private fun initView() {
        viewPagerMain.offscreenPageLimit = 2
        viewPagerMain.isUserInputEnabled = false
        viewPagerMain.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return 3
            }

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> InfoFragment.newInstance()
                    1 -> HomeFragment.newInstance()
                    else -> SettingsFragment.newInstance()
                }
            }
        }
        viewPagerMain.currentItem = 1

        TabLayoutMediator(
            tabLayout, viewPagerMain
        ) { tab, position ->

            when (position) {
                0 -> {
                    tab.text = getString(R.string.txt_info)
                    tab.setIcon(R.drawable.ic_info)
                }
                1 -> {
                    tab.text = getString(R.string.txt_home)
                    tab.setIcon(R.drawable.ic_home)
                }
                else -> {
                    tab.text = getString(R.string.txt_setting)
                    tab.setIcon(R.drawable.ic_setting)
                }
            }
        }.attach()

    }

    fun showScreen(intent: Intent) {
        mIntent = null
        mIntent = intent
        startActivity(mIntent)
    }

    private fun handleEvent() {
        imgFlag.setOnClickListener {
            val intent = Intent(this, LanguageActivity::class.java)
            showScreen(intent)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val nanoTime = System.nanoTime()
                if (nanoTime - mExitTime < 2e9) {
                    finish()
                } else {
                    mExitTime = nanoTime
                    showNotice(getString(R.string.txt_press_again_to_exit))
                }
            }
        })

        btnTabInfo.setOnClickListener {
            viewPagerMain.setCurrentItem(0, false)
            resetColorTabLayout()

            val green = ContextCompat.getColor(this, R.color.colorPrimary)
            tvInfo.setTextColor(green)
            imgInfo.setColorFilter(green)
        }

        btnTabHome.setOnClickListener {
            viewPagerMain.setCurrentItem(1, false)
            resetColorTabLayout()

            val green = ContextCompat.getColor(this, R.color.colorPrimary)
            tvHome.setTextColor(green)
            imgHome.setColorFilter(green)

        }

        btnTabSettings.setOnClickListener {
            viewPagerMain.setCurrentItem(2, false)
            resetColorTabLayout()

            val green = ContextCompat.getColor(this, R.color.colorPrimary)
            tvSettings.setTextColor(green)
            imgSettings.setColorFilter(green)
        }
    }

    private fun resetColorTabLayout() {
        tvHome.setTextColor(Color.BLACK)
        imgHome.setColorFilter(Color.BLACK)
        tvInfo.setTextColor(Color.BLACK)
        imgInfo.setColorFilter(Color.BLACK)
        tvSettings.setTextColor(Color.BLACK)
        imgSettings.setColorFilter(Color.BLACK)
    }

}