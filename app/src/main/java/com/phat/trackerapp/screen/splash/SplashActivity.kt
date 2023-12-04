package com.phat.trackerapp.screen.splash

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.phat.trackerapp.R
import com.phat.trackerapp.database.SharePrefDB
import com.phat.trackerapp.screen.home.HomeActivity
import com.phat.trackerapp.screen.language.activity.LanguageActivity
import com.phat.trackerapp.utils.Utils
import kotlinx.android.synthetic.main.dialog_update_version.btnBack
import kotlinx.android.synthetic.main.dialog_update_version.btnDoLater
import kotlinx.android.synthetic.main.dialog_update_version.btnGotoStore


class SplashActivity : AppCompatActivity() {
    private lateinit var mSharePrefDB: SharePrefDB

    private var mIntent: Intent? = null

    private var mWarningDialog: Dialog? = null

    private var isCloseApp = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setLanguageForApp(this)
        setContentView(R.layout.activity_splash)
        initView()
        initData()
        handleEvent()

    }

    private fun initView() {
        mWarningDialog = Utils.onCreateDialog(this,R.layout.dialog_update_version,false)
    }

    private fun initData() {
        mSharePrefDB = SharePrefDB(this)
        showScreen()
    }


    private fun showScreen() {
        mIntent = if (Utils.isGetLanguage(this)) {
            Intent(this, HomeActivity::class.java)
        } else {
            Intent(this, LanguageActivity::class.java)
        }
        startActivity(mIntent)
        finish()

    }

    private fun handleEvent() {
        mWarningDialog?.btnGotoStore?.setOnClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        mWarningDialog?.btnBack?.setOnClickListener {
            if (isCloseApp) {
                finish()
            } else {
                showScreen()
            }
        }

        mWarningDialog?.btnDoLater?.setOnClickListener {
            mWarningDialog?.dismiss()
            showScreen()
        }
    }

}