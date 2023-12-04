package com.phat.trackerapp.screen.language.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.phat.trackerapp.R
import com.phat.trackerapp.database.SharePrefDB
import com.phat.trackerapp.screen.home.HomeActivity
import com.phat.trackerapp.screen.language.adapter.LanguageAdapter
import com.phat.trackerapp.screen.language.model.Country
import com.phat.trackerapp.screen.yourdata.YourDataActivity
import com.phat.trackerapp.utils.Utils
import kotlinx.android.synthetic.main.activity_language.btnTickLanguage
import kotlinx.android.synthetic.main.activity_language.rvLanguage
import java.util.Locale

class LanguageActivity : AppCompatActivity(), LanguageAdapter.OnItemLanguageListener {

    private lateinit var mItemLanguageAdapter: LanguageAdapter

    private lateinit var mLanguages: ArrayList<Country>

    var mPosCheck = 0

    private var mIOSCountry =
        listOf("en", "es", "fr", "hi", "vi", "pt", "de", "it", "ja", "ar", "iw", "ko", "nl")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)
        initData()
        handleEvents()
    }


    override fun onItemLanguageClick(position: Int) {
        Log.d("111222222", position.toString() + "   " + mPosCheck)
        mPosCheck = position
        mItemLanguageAdapter.notifyDataSetChanged()
    }

    private fun initData() {
        mLanguages = ArrayList()

        mLanguages.add(Country(R.drawable.ic_flag_english, getString(R.string.english), locale =  "en"))
        mLanguages.add(Country(R.drawable.ic_flag_vietnam, getString(R.string.vietnamese), locale ="vi"))


        val defaultCountry = mLanguages.find { it.locale == Locale.getDefault().language }
        if(defaultCountry != null){
            Log.d("113333", defaultCountry.locale)
            if (defaultCountry.locale != "en") {
                mLanguages.remove(defaultCountry)
                mLanguages.add(1, defaultCountry)
            }
        }

        mPosCheck = mLanguages.indexOfFirst { it.locale == Utils.getIOSCountryData(applicationContext)}

        if(mPosCheck < 0 || mPosCheck >= mLanguages.size){
            mPosCheck = 0
        }
        Log.d("23333333", mPosCheck.toString())

        mItemLanguageAdapter = LanguageAdapter(this, mLanguages, this)
        rvLanguage.adapter = mItemLanguageAdapter
    }

    private fun handleEvents() {
        btnTickLanguage.setOnClickListener {
            Utils.isSetLanguage(this)
            Utils.setIOSCountryData(mLanguages[mPosCheck].locale, this)
            Utils.setCurrentFlag(mLanguages[mPosCheck].icon, this)
            val sharePrefDB = SharePrefDB(this)
            val intent = if(sharePrefDB.getUser() == null){
                Intent(this, YourDataActivity::class.java)
            }else{
                Intent(this, HomeActivity::class.java)
            }
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}