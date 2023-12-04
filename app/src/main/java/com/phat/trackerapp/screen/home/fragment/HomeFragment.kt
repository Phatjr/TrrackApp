package com.phat.trackerapp.screen.home.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.phat.trackerapp.R
import com.phat.trackerapp.screen.bloodpressure.TrackerActivity
import com.phat.trackerapp.screen.bloodsugar.BloodSugarActivity
import com.phat.trackerapp.screen.bmi.activity.BMIActivity
import com.phat.trackerapp.screen.heartrate.HeartRateActivity
import com.phat.trackerapp.screen.home.HomeActivity
import com.phat.trackerapp.screen.scanner.FoodScannerActivity
import com.phat.trackerapp.utils.Constants
import com.phat.trackerapp.utils.Utils
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_dialog_comming_soon.*
import java.lang.Integer.max


class HomeFragment : Fragment() {
    private lateinit var mUpdateSoonDialog: Dialog

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        handleEvent()

//        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out_shop)
//        imghHeartRate.startAnimation(animation)
    }

    private fun initView() {
        try {
            if(Utils.isEnglishLanguage()){
                tvHeartRate.text = Constants.HEART_RATE
            }

            mUpdateSoonDialog =
                Utils.onCreateDialog(requireContext(), R.layout.layout_dialog_comming_soon, true)
            if (btnFoodScanner != null && btnBloodSugar != null && btnBloodPressure != null && btnHeartRate != null) {
                btnFoodScanner.post {
                    val max1 = max(btnFoodScanner.height, btnBloodSugar.height)
                    val max2 = max(btnHeartRate.height, btnFoodScanner.height)
                    val max = max(max1, max2)
                    val params: LinearLayout.LayoutParams =
                        btnFoodScanner.layoutParams as LinearLayout.LayoutParams
                    params.height = max

                    btnFoodScanner.setLayoutParams(params)
                    btnBloodPressure.setLayoutParams(params)
//                    btnHeartRate.setLayoutParams(params)
//                    btnBMI.setLayoutParams(params)
                }
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    private fun handleEvent() {
        btnHeartRate.setOnClickListener {

            val intent = Intent(requireContext(), HeartRateActivity::class.java)
            if (context is HomeActivity) {
                (context as HomeActivity).showScreen(intent)
            }
        }

        btnBloodPressure.setOnClickListener {

            val intent = Intent(requireContext(), TrackerActivity::class.java)
            if (context is HomeActivity) {
                (context as HomeActivity).showScreen(intent)
            }
        }

        btnBloodSugar.setOnClickListener {

            val intent = Intent(requireContext(), BloodSugarActivity::class.java)
            if (context is HomeActivity) {
                (context as HomeActivity).showScreen(intent)
            }
        }

        btnBMI.setOnClickListener {

            val intent = Intent(requireContext(), BMIActivity::class.java)
            if (context is HomeActivity) {
                (context as HomeActivity).showScreen(intent)
            }
        }

        btnFoodScanner.setOnClickListener {

            val intent = Intent(requireContext(), FoodScannerActivity::class.java)
            if (context is HomeActivity) {
                (context as HomeActivity).showScreen(intent)
            }
        }

        mUpdateSoonDialog.apply {
            btnClose.setOnClickListener {
                dismiss()
            }
        }
    }
}