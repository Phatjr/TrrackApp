package com.phat.trackerapp.screen.heartrate.chart

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.horen.chart.barchart.IBarData
import com.phat.trackerapp.R
import com.phat.trackerapp.data.database.HeartRateDatabase
import com.phat.trackerapp.extension.executeAsyncTask
import com.phat.trackerapp.extension.onCreateBarChartOneColumn
import com.phat.trackerapp.screen.home.model.TrackerBarData
import com.phat.trackerapp.utils.Utils
import kotlinx.android.synthetic.main.activity_chart.btnBack
import kotlinx.android.synthetic.main.activity_chart.chartHeartRate
import kotlinx.android.synthetic.main.activity_chart.txtNoChart

class ChartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)
        Utils.changeStatusBarColor(this, R.color.red)
        initData()
        initView()
        handleEvent()
    }



    private fun initData() {
        lifecycleScope.executeAsyncTask(onPreExecute = {

        }, doInBackground = {
            HeartRateDatabase.getInstance(this).heartRateDao().getAll()
        }, onPostExecute = {
            val mList = it
            if(mList.isEmpty()){
                txtNoChart.visibility = View.VISIBLE
            }else{
                txtNoChart.visibility = View.GONE
            }
            //chart
            val data: ArrayList<IBarData> = ArrayList()

            for (element in mList) {
                data.add(
                    TrackerBarData(
                        element.time +", "+element.time,
                        element.heartRate.toFloat()
                    )
                )
            }

            val xDays = ArrayList<String>()
            xDays.add(getString(R.string.txt_heart_rate))
            onCreateBarChartOneColumn(data,xDays,chartHeartRate)
        })

    }

    private fun initView() {

    }

    private fun handleEvent() {
        btnBack.setOnClickListener {
            finish()
        }
    }
}