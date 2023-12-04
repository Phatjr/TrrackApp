package com.phat.trackerapp.screen.heartrate

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.blood.pressure.utils.measuring.RecorderVoice
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.phat.trackerapp.R
import com.phat.trackerapp.extension.showNotice
import com.phat.trackerapp.screen.heartrate.fragment.AnimationFragment
import com.phat.trackerapp.screen.heartrate.fragment.InstructionFragment
import com.phat.trackerapp.screen.record.heartrate.EditAddHeartRateActivity
import com.phat.trackerapp.utils.Constants
import com.phat.trackerapp.utils.measuring.CustomCamera
import kotlinx.android.synthetic.main.activity_measure.btnBack
import kotlinx.android.synthetic.main.activity_measure.btnFlash
import kotlinx.android.synthetic.main.activity_measure.btnSound
import kotlinx.android.synthetic.main.activity_measure.btnStartMeasure
import kotlinx.android.synthetic.main.activity_measure.graphPulse
import kotlinx.android.synthetic.main.activity_measure.layoutMeasure
import kotlinx.android.synthetic.main.activity_measure.layoutPreview
import kotlinx.android.synthetic.main.activity_measure.layoutStart
import kotlinx.android.synthetic.main.activity_measure.preview
import kotlinx.android.synthetic.main.activity_measure.progressBar
import kotlinx.android.synthetic.main.activity_measure.relativeLayout
import kotlinx.android.synthetic.main.activity_measure.tabLayout
import kotlinx.android.synthetic.main.activity_measure.tvGuideline
import kotlinx.android.synthetic.main.activity_measure.tvMeasuring
import kotlinx.android.synthetic.main.activity_measure.tvPutFingerOnCam
import kotlinx.android.synthetic.main.activity_measure.tvValue
import kotlinx.android.synthetic.main.activity_measure.viewpager


class MeasureActivity : AppCompatActivity() {
    private lateinit var mHandler: Handler

    private lateinit var mRunnable: Runnable

    companion object {
        private const val REQUEST_CODE_CAMERA = 0

        const val MESSAGE_UPDATE_REALTIME: Int = 1

        const val MESSAGE_STOP_MEASUREMENT: Int = 4

        const val MESSAGE_RESULT: Int = 2

        const val MESSAGE_ERROR: Int = 3
    }

    private lateinit var camera: CustomCamera

    private lateinit var recorderVoice: RecorderVoice

    private var mIntent: Intent? = null

    private val mainHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == MESSAGE_UPDATE_REALTIME) {
                val progress = msg.obj as Int
                progressBar.setProgress(progress)
                if (progress > 0) {
                    if (progress < 14 && btnSound.tag.equals("on")) {
                        recorderVoice.playBeep()
                    }

                    graphPulse.visibility = View.VISIBLE
//                    Glide.with(this@MeasureActivity).load(R.drawable.giphy)
//                        .into(graphPulse)
                    tvPutFingerOnCam.clearAnimation()
                    tvPutFingerOnCam.text = getString(R.string.txt_measure_your_heart)

                    if (progressBar.progress == 14f) {
                        gotoNextScreen()
                    }
                } else {
                    if (btnSound.tag.equals("on")) {
                        recorderVoice.stopPlay()
                    }
                    graphPulse.visibility = View.GONE
                }

            }
            if (msg.what == MESSAGE_STOP_MEASUREMENT) {
                progressBar.setProgress(0)
                tvPutFingerOnCam.text = getString(R.string.txt_place)
                tvPutFingerOnCam.clearAnimation()
                setAlphaAnimation()
                graphPulse.visibility = View.GONE
            }
            if (msg.what == MESSAGE_RESULT) {
                // make sure menu items are enabled when it opens.
                tvValue.text = msg.obj.toString()
                if (progressBar.progress == 14f) {
//                    val returnIntent = Intent()
//                    returnIntent.putExtra(Constants.VALUE, tvValue.text.toString().toInt())
//                    setResult(RESULT_OK, returnIntent)
                    gotoNextScreen()
                }
            }
            if (msg.what == MESSAGE_ERROR) {
                Log.println(Log.WARN, "camera", msg.obj.toString())
            }
        }
    }

    private fun gotoNextScreen() {
        val intent = Intent(this@MeasureActivity, EditAddHeartRateActivity::class.java)
        intent.putExtra(Constants.VALUE, tvValue.text.toString().toInt())
        intent.putExtra(Constants.FROM_TRACKER, true)
        showScreen(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measure)
        initData()
        initView()
        handleEvent()
    }


    private fun initData() {
        recorderVoice = RecorderVoice(this)
        camera = CustomCamera(this, mainHandler)
        if (isRequestGranted()) {
            requestCamera()
        }
    }

    private fun showScreen(intent: Intent? = null){
        mIntent = null
        mIntent = intent
        startActivity(mIntent)
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {

        if (requestCode == REQUEST_CODE_CAMERA) {
            if (!(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Snackbar.make(
                    relativeLayout,
                    getString(R.string.txt_request_camera),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun initView() {
        viewpager.offscreenPageLimit = 2
        viewpager.isUserInputEnabled = false
        viewpager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return 2
            }

            override fun createFragment(position: Int): Fragment {
                return if (position == 0) InstructionFragment.newInstance()
                else AnimationFragment.newInstance()
            }
        }

        TabLayoutMediator(
            tabLayout, viewpager
        ) { tab, position ->
            if (position == 0) {
                tab.text = getString(R.string.txt_instruction)
            } else {
                tab.text = getString(R.string.txt_animation)
            }
        }.attach()

        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_out_shop)
        btnStartMeasure.startAnimation(animation)
    }

    private fun setAlphaAnimation() {
        val alphaAnimation = AlphaAnimation(0f, 1.0f)
        alphaAnimation.duration = 400
        alphaAnimation.repeatMode = 2
        alphaAnimation.repeatCount = Animation.INFINITE

        tvPutFingerOnCam.startAnimation(alphaAnimation)
    }

    override fun onPause() {
        super.onPause()
        camera.stop()
        recorderVoice.stopPlay()
        mainHandler.removeCallbacksAndMessages(null)
    }

    private fun setTextRed() {
        tvGuideline.text = HtmlCompat.fromHtml(
            tvGuideline.text.toString().replace("red", "<font color='#EE403F'>red</font>"),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    override fun onBackPressed() {
        handleBack()
    }

    private fun handleBack() {
        if (layoutMeasure.visibility == View.VISIBLE) {
            layoutStart.visibility = View.VISIBLE
            layoutMeasure.visibility = View.GONE
            btnSound.visibility = View.GONE
            btnFlash.visibility = View.GONE
            camera.stop()
        } else {
            val intent = Intent(this, HeartRateActivity::class.java)
            showScreen(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isRequestGranted()) {
            requestCamera()
        } else {
            if (!camera.initCamera()) {
                showNotice("Fail to open Camera. Please try again later")
            }
        }
    }

    private fun onPreviewCamera() {
        if (isRequestGranted()) {
            requestCamera()
        } else {
            if (!camera.initCamera()) {
                showNotice("Fail to open Camera. Please try again later")
            }
            if (layoutPreview != null && preview != null) {
                layoutPreview.removeAllViews()
                layoutPreview.addView(preview)
            }
            camera.startPreview(preview.holder)
        }
    }

    private fun requestCamera() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.CAMERA),
            REQUEST_CODE_CAMERA
        )
    }

    private fun isRequestGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED
    }

    private fun handleEvent() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    viewpager.setCurrentItem(0, false)
                } else {
                    viewpager.setCurrentItem(1, false)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        btnBack.setOnClickListener {
            handleBack()
        }

        btnStartMeasure.setOnClickListener {

            mRunnable = LoadingTextView()
            mHandler = Handler()
            mHandler.postDelayed(mRunnable, 1000)

            setAlphaAnimation()
            setTextRed()

            layoutStart.visibility = View.GONE
            layoutMeasure.visibility = View.VISIBLE
            btnSound.visibility = View.VISIBLE
            btnFlash.visibility = View.VISIBLE
            onPreviewCamera()
        }

        btnFlash.setOnClickListener {
            if (btnFlash.tag.equals("on")) {
                btnFlash.tag = "off"
                btnFlash.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.color_disable_btn_measure
                    )
                )
                camera.turnOffFlash()
            } else {
                btnFlash.tag = "on"
                btnFlash.setColorFilter(ContextCompat.getColor(this, R.color.black))
                camera.turnOnFlash()
            }
        }

        btnSound.setOnClickListener {
            val recorderVoice = RecorderVoice(this)
            recorderVoice.playBeep()
            if (btnSound.tag.equals("on")) {
                btnSound.tag = "off"
                btnSound.setImageResource(R.drawable.ic_sound_off)
                btnSound.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.color_disable_btn_measure
                    )
                )
            } else {
                btnSound.tag = "on"
                btnSound.setImageResource(R.drawable.ic_sound_on)
                btnSound.setColorFilter(ContextCompat.getColor(this, R.color.black))
            }
        }
    }

    inner class LoadingTextView : Runnable {
        var mCount = 1
        override fun run() {
            when (mCount) {
                1 -> {
                    mCount = 2
                    tvMeasuring.setText(getString(R.string.txt_measure1) + ".")
                }
                2 -> {
                    mCount = 3
                    tvMeasuring.setText(getString(R.string.txt_measure1) + "..")
                }
                else -> {
                    mCount = 1
                    tvMeasuring.setText(getString(R.string.txt_measure1))
                }
            }
            mHandler.postDelayed(mRunnable, 400)
        }

    }


}