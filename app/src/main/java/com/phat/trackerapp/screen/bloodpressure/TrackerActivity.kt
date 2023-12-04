package com.phat.trackerapp.screen.bloodpressure

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.horen.chart.barchart.IBarData
import com.phat.trackerapp.R
import com.phat.trackerapp.callback.OnItemListener
import com.phat.trackerapp.data.database.TrackerDatabase
import com.phat.trackerapp.data.model.Tracker
import com.phat.trackerapp.extension.executeAsyncTask
import com.phat.trackerapp.extension.onCreateBarChart
import com.phat.trackerapp.screen.bloodpressure.history.activity.HistoryActivity
import com.phat.trackerapp.screen.bloodpressure.history.adapter.HistoryAdapter
import com.phat.trackerapp.screen.home.model.TrackerBarData
import com.phat.trackerapp.screen.record.tracker.AddNewTrackerActivity
import com.phat.trackerapp.screen.record.tracker.EditTrackerActivity
import com.phat.trackerapp.utils.Constants
import com.phat.trackerapp.utils.Utils
import kotlinx.android.synthetic.main.activity_tracker.btnAdd
import kotlinx.android.synthetic.main.activity_tracker.chartTracker
import kotlinx.android.synthetic.main.activity_tracker.itemDiastolic
import kotlinx.android.synthetic.main.activity_tracker.itemPulse
import kotlinx.android.synthetic.main.activity_tracker.itemSystolic
import kotlinx.android.synthetic.main.activity_tracker.layoutHistory
import kotlinx.android.synthetic.main.activity_tracker.rvLastTracker
import kotlinx.android.synthetic.main.activity_tracker.txtNoChart
import kotlinx.android.synthetic.main.layout_dialog_add_new_record.btnAddRecord
import kotlinx.android.synthetic.main.layout_dialog_add_new_record.btnClose
import kotlinx.android.synthetic.main.layout_item_filter.btnNext
import kotlinx.android.synthetic.main.layout_item_filter.btnPrev
import kotlinx.android.synthetic.main.layout_item_filter.txtStateValue
import kotlinx.android.synthetic.main.layout_toolbar.btnBack
import kotlinx.android.synthetic.main.layout_toolbar.btnHistory
import kotlinx.coroutines.cancel
import kotlin.math.roundToInt

class TrackerActivity : AppCompatActivity(), OnItemListener {
    private lateinit var mTrackers: ArrayList<Tracker>

    private var mCurrentIndex = 0

    private lateinit var mStateValues: Array<String>

    private var mMinSystolic = 0
    private var mMaxSystolic = 0
    private var mAverSystolic = 0

    private var mMinDiastolic = 0
    private var mMaxDiastolic = 0
    private var mAverDiastolic = 0

    private var mMinPulse = 0
    private var mMaxPulse = 0
    private var mAverPulse = 0

    private lateinit var mLastTrackers: ArrayList<Tracker>

    private lateinit var mHistoryAdapter: HistoryAdapter

    private var mTrackerLatest: Tracker? = null

    private var mIntent: Intent? = null

    private var mIsBackScreen = false

    private lateinit var mAddNewRecordDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker)
        initData()
        handleEvent()
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.cancel()
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadData()
        }
    }

    override fun onItemClick(position: Int) {
        if(position < mLastTrackers.size){
            val tracker = mLastTrackers[position]
            val intent = Intent(this, EditTrackerActivity::class.java)
            intent.putExtra(Constants.ID, tracker.id)
            intent.putExtra(Constants.TYPE, Constants.HOME)
            showScreen(intent)
        }
    }

    private fun showScreen(intent: Intent? = null){
        mIntent = null
        mIntent = intent
        if(mIsBackScreen) finish()
        else{
            resultLauncher.launch(mIntent)
        }
    }

    private fun initData() {
        mTrackers = ArrayList()
        mLastTrackers = ArrayList()
        mAddNewRecordDialog = Utils.onCreateDialog(this, R.layout.layout_dialog_add_new_record)

        mHistoryAdapter = HistoryAdapter(this, mLastTrackers, this)
        rvLastTracker.adapter = mHistoryAdapter

        loadData()
    }

    private fun loadData(){
        lifecycleScope.executeAsyncTask(onPreExecute = {
            mTrackers.clear()
            mLastTrackers.clear()
            // ...
        }, doInBackground = {
            TrackerDatabase.getInstance(this).trackerDao().getAll()
        }, onPostExecute = {
            // ... here "it" is a data returned from "doInBackground"
            val trackerAll = it
            if (trackerAll != null && trackerAll.isNotEmpty()) {
                mHistoryAdapter.notifyDataSetChanged()
                txtNoChart.visibility = View.GONE
                chartTracker.visibility = View.VISIBLE
                mTrackers.addAll(trackerAll)
                mTrackerLatest = mTrackers[mTrackers.size - 1]
                if (mTrackers.size >= 3) {
                    mLastTrackers.add(mTrackers[mTrackers.size - 1])
                    mLastTrackers.add(mTrackers[mTrackers.size - 2])
                    mLastTrackers.add(mTrackers[mTrackers.size - 3])
                } else if (mTrackers.size >= 2) {
                    mLastTrackers.add(mTrackers[mTrackers.size - 1])
                    mLastTrackers.add(mTrackers[mTrackers.size - 2])
                } else if (mTrackers.size > 0) {
                    mLastTrackers.add(mTrackers[mTrackers.size - 1])
                } else {

                }
                mTrackers.sortBy { it.getTimeLong() }
            } else {
                txtNoChart.visibility = View.VISIBLE
                chartTracker.visibility = View.INVISIBLE
                mAddNewRecordDialog.show()
                itemSystolic.value = "0"
                itemDiastolic.value = "0"
                itemPulse.value = "0"
            }
            mStateValues = arrayOf(
                getString(R.string.txt_max),
                getString(R.string.txt_min),
                getString(R.string.txt_average),
                getString(R.string.txt_latest)
            )
            // find min max
            if (mTrackers.size > 0) {
                mMinSystolic = mTrackers.reduce(Compare::minSystolic).systolic
                mMaxSystolic = mTrackers.reduce(Compare::maxSystolic).systolic
                mAverSystolic = ((mMaxSystolic + mMinSystolic) / 2f).roundToInt()

                mMinDiastolic = mTrackers.reduce(Compare::minDiastolic).diastolic
                mMaxDiastolic = mTrackers.reduce(Compare::maxDiastolic).diastolic
                mAverDiastolic = ((mMaxDiastolic + mMinDiastolic) / 2f).roundToInt()

                mMinPulse = mTrackers.reduce(Compare::minPulse).pulse
                mMaxPulse = mTrackers.reduce(Compare::maxPulse).pulse
                mAverPulse = ((mMaxPulse + mMinPulse) / 2f).roundToInt()
                updateValueMinMaxAver()
            }

            // chart

            val xDays = ArrayList<String>()
            xDays.add(getString(R.string.txt_systolic))
            xDays.add(getString(R.string.txt_diastolic))
            val data: ArrayList<List<IBarData>> = ArrayList()
            val systolics: ArrayList<TrackerBarData> = ArrayList()
            val diastolics: ArrayList<TrackerBarData> = ArrayList()

            val labels: ArrayList<String> = ArrayList()

            for (i in 0 until mTrackers.size) {
                val tracker = mTrackers[i]

                val label = tracker.time + ", " + tracker.date

                if(!labels.contains(label)){
                    labels.add(label)
                    diastolics.add(TrackerBarData(label, tracker.diastolic.toFloat()))
                    systolics.add(
                        TrackerBarData(
                            tracker.time + ", " + tracker.date,
                            tracker.systolic.toFloat()
                        )
                    )
                }else{
                    val label1 = (tracker.time) + ", " + tracker.date+"*"
                    labels.add(label1)
                    diastolics.add(TrackerBarData(label1, tracker.diastolic.toFloat()))
                    systolics.add(
                        TrackerBarData(
                            label1,
                            tracker.systolic.toFloat()
                        )
                    )
                }
            }
            data.add(systolics)
            data.add(diastolics)

            onCreateBarChart(data, xDays,chartTracker)

        })
    }

    private fun handleEvent() {
        btnAdd.setOnClickListener {
            val intent = Intent(this@TrackerActivity, AddNewTrackerActivity::class.java)
            showScreen(intent)
        }

        btnHistory.setOnClickListener {

            val mIntent = Intent(this, HistoryActivity::class.java)
            showScreen(mIntent)
        }

        btnBack.setOnClickListener {
            mIsBackScreen = true
            showScreen()
        }

        layoutHistory.setOnClickListener {

            val mIntent = Intent(this, HistoryActivity::class.java)
            showScreen(mIntent)
        }

        btnNext.setOnClickListener {

            if (mCurrentIndex == 3) {
                mCurrentIndex = 0
            } else {
                mCurrentIndex++
            }
            txtStateValue.text = mStateValues[mCurrentIndex]
            updateValueMinMaxAver()
        }

        btnPrev.setOnClickListener {

            if (mCurrentIndex == 0) {
                mCurrentIndex = 3
            } else {
                mCurrentIndex--
            }
            txtStateValue.text = mStateValues[mCurrentIndex]
            updateValueMinMaxAver()
        }

        mAddNewRecordDialog.apply {
            btnAddRecord.setOnClickListener {
                dismiss()
                val intent = Intent(this@TrackerActivity, AddNewTrackerActivity::class.java)
                showScreen(intent)
            }

            btnClose.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun updateValueMinMaxAver() {
        when (mCurrentIndex) {
            0 -> {
                itemSystolic.value = mMaxSystolic.toString()
                itemDiastolic.value = mMaxDiastolic.toString()
                itemPulse.value = mMaxPulse.toString()
            }

            1 -> {
                itemSystolic.value = mMinSystolic.toString()
                itemDiastolic.value = mMinDiastolic.toString()
                itemPulse.value = mMinPulse.toString()
            }

            2 -> {
                itemSystolic.value = mAverSystolic.toString()
                itemDiastolic.value = mAverDiastolic.toString()
                itemPulse.value = mAverPulse.toString()
            }

            3 -> {
                if (mTrackerLatest != null && mTrackerLatest?.systolic != null) {
                    itemSystolic.value = mTrackerLatest?.systolic.toString()
                    itemDiastolic.value = mTrackerLatest?.diastolic.toString()
                    itemPulse.value = mTrackerLatest?.pulse.toString()
                }
            }
        }
    }


    internal object Compare {
        fun minSystolic(a: Tracker, b: Tracker): Tracker {
            return if (a.systolic < b.systolic) a else b
        }

        fun maxSystolic(a: Tracker, b: Tracker): Tracker {
            return if (a.systolic > b.systolic) a else b
        }

        fun minDiastolic(a: Tracker, b: Tracker): Tracker {
            return if (a.systolic < b.systolic) a else b
        }

        fun maxDiastolic(a: Tracker, b: Tracker): Tracker {
            return if (a.diastolic > b.diastolic) a else b
        }

        fun minPulse(a: Tracker, b: Tracker): Tracker {
            return if (a.systolic < b.systolic) a else b
        }

        fun maxPulse(a: Tracker, b: Tracker): Tracker {
            return if (a.pulse > b.pulse) a else b
        }

    }
}