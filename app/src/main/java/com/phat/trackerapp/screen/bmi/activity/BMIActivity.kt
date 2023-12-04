package com.phat.trackerapp.screen.bmi.activity

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
import com.phat.trackerapp.data.database.BMIDatabase
import com.phat.trackerapp.data.model.BMI
import com.phat.trackerapp.extension.executeAsyncTask
import com.phat.trackerapp.extension.onCreateBarChart
import com.phat.trackerapp.screen.bmi.adapter.BMIAdapter
import com.phat.trackerapp.screen.home.model.TrackerBarData
import com.phat.trackerapp.screen.record.bmi.EditAddBMIActivity
import com.phat.trackerapp.utils.Constants
import com.phat.trackerapp.utils.Utils
import kotlinx.android.synthetic.main.activity_bmi.*
import kotlinx.android.synthetic.main.layout_dialog_add_new_record.*
import kotlinx.android.synthetic.main.layout_item_filter.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.coroutines.cancel

class BMIActivity : AppCompatActivity() , OnItemListener{
    private lateinit var mAddNewRecordDialog: Dialog

    private lateinit var mBMIs: ArrayList<BMI>
    
    private lateinit var mLastBMIs: ArrayList<BMI>

    private var mLatestBMI: BMI? = null

    private lateinit var mStateValues: Array<String>

    private lateinit var mBMIAdapter: BMIAdapter

    private var mMinHeight = 0f
    private var mMaxHeight = 0f
    private var mAverHeight = 0f

    private var mMinWeight = 0f
    private var mMaxWeight = 0f
    private var mAverWeight = 0f

    private var mMinBmi = 0f
    private var mMaxBmi = 0f
    private var mAverBmi = 0f

    private var mCurrentIndex: Int = 0

    private var mIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmi)
        initData()
        handleEvent()
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.cancel()
    }

    private fun initData() {
        txtTitle.text = getString(R.string.txt_weight_and_bmi)
        mAddNewRecordDialog = Utils.onCreateDialog(this, R.layout.layout_dialog_add_new_record)
        mBMIs = ArrayList()
        mLastBMIs = ArrayList()

        mBMIAdapter = BMIAdapter(this, mLastBMIs, this)
        rvLastBMI.adapter = mBMIAdapter

        loadData()
    }

    private fun loadData(){
        lifecycleScope.executeAsyncTask(onPreExecute = {
            mBMIs.clear()
            mLastBMIs.clear()
        }, doInBackground = {
            BMIDatabase.getInstance(this).BMIDao().getAll()
        }, onPostExecute = {
            mBMIAdapter.notifyDataSetChanged()
            val bmiAll = it
            if (bmiAll != null && bmiAll.isNotEmpty()) {
                txtNoChart.visibility = View.GONE
                chartBMI.visibility = View.VISIBLE
                mBMIs.addAll(bmiAll)
                mLatestBMI = mBMIs[mBMIs.size - 1]
                if (mBMIs.size >= 3) {
                    mLastBMIs.add(mBMIs[mBMIs.size - 1])
                    mLastBMIs.add(mBMIs[mBMIs.size - 2])
                    mLastBMIs.add(mBMIs[mBMIs.size - 3])
                } else if (mBMIs.size >= 2) {
                    mLastBMIs.add(mBMIs[mBMIs.size - 1])
                    mLastBMIs.add(mBMIs[mBMIs.size - 2])
                } else if (mBMIs.size > 0) {
                    mLastBMIs.add(mBMIs[mBMIs.size - 1])
                } else {

                }
                mBMIs.sortBy { it.getTimeLong() }
            } else {
                txtNoChart.visibility = View.VISIBLE
                chartBMI.visibility = View.INVISIBLE
                mAddNewRecordDialog.show()
                txtHeightValue.text = "0"
                txtWeightValue.text = "0"
                txtBmiValue.text = "0"
            }
            mStateValues = arrayOf(
                getString(R.string.txt_max),
                getString(R.string.txt_min),
                getString(R.string.txt_average),
                getString(R.string.txt_latest)
            )
            // find min max
            if (mBMIs.size > 0) {
                mMinHeight = mBMIs.reduce(Compare::minHeight).height
                mMaxHeight = mBMIs.reduce(Compare::maxHeight).height
                mAverHeight = ((mMaxHeight + mMinHeight) / 2f)

                mMinWeight = mBMIs.reduce(Compare::minWeight).weight
                mMaxWeight = mBMIs.reduce(Compare::maxWeight).weight
                mAverWeight = ((mMaxWeight + mMinWeight) / 2f)

                val minBMI = mBMIs.reduce(Compare::minBMI)
                mMinBmi =  Utils.calculateBMI(minBMI.weight,minBMI.height)
                val maxBMI = mBMIs.reduce(Compare::maxBMI)
                mMaxBmi = Utils.calculateBMI(maxBMI.weight,maxBMI.height)
                mAverBmi = ((mMaxBmi + mMinBmi) / 2f)
                updateValueMinMaxAver()
            }

            // chart

            val xDays = ArrayList<String>()
            xDays.add(getString(R.string.txt_weight))
            xDays.add(getString(R.string.txt_bmi))

            val data: ArrayList<List<IBarData>> = ArrayList()
            val weights: ArrayList<TrackerBarData> = ArrayList()
            val bmis: ArrayList<TrackerBarData> = ArrayList()

            for (i in 0 until mBMIs.size) {
                val bmi = mBMIs[i]
                weights.add(
                    TrackerBarData(
                        bmi.time +","+bmi.date,
                        bmi.weight.toFloat()
                    )
                )
                bmis.add( TrackerBarData(
                    bmi.time +","+bmi.date,
                    bmi.height.toFloat()
                ))
            }
            data.add(weights)
            data.add(bmis)

            onCreateBarChart(data,xDays,chartBMI)
        })
    }

    private fun showScreen(intent: Intent){
        mIntent = null
        mIntent = intent
        resultLauncher.launch(mIntent)
    }

    private fun handleEvent() {
        btnBack.setOnClickListener {
            finish()
        }

        btnAdd.setOnClickListener {
            val intent = Intent(this@BMIActivity, EditAddBMIActivity::class.java)
            showScreen(intent)
        }

        btnHistory.setOnClickListener {
            val intent = Intent(this, HistoryBMIActivity::class.java)
            showScreen(intent)
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
                val intent = Intent(this@BMIActivity, EditAddBMIActivity::class.java)
                showScreen(intent)
            }

            btnClose.setOnClickListener {
                dismiss()
            }
        }
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadData()
        }
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this@BMIActivity, EditAddBMIActivity::class.java)
        intent.putExtra(Constants.ID, mLastBMIs[position].id)
        showScreen(intent)
    }

    private fun updateValueMinMaxAver() {
        when (mCurrentIndex) {
            0 -> {
                txtHeightValue.text = mMaxHeight.toString()
                txtWeightValue.text = mMaxWeight.toString()
                txtBmiValue.text = mMaxBmi.toString()
            }

            1 -> {
                txtHeightValue.text = mMinHeight.toString()
                txtWeightValue.text = mMinWeight.toString()
                txtBmiValue.text = mMinBmi.toString()
            }

            2 -> {
                txtHeightValue.text = mAverHeight.toString()
                txtWeightValue.text = mAverWeight.toString()
                txtBmiValue.text = mAverBmi.toString()
            }

            3 -> {
                if (mLatestBMI != null && mLatestBMI?.height != null) {
                    txtHeightValue.text = mLatestBMI?.height.toString()
                    txtWeightValue.text = mLatestBMI?.weight.toString()
                    txtBmiValue.text = Utils.calculateBMI(mLatestBMI!!.weight,mLatestBMI!!.height).toString()
                }
            }
        }
    }

    internal object Compare {
        fun minHeight(a: BMI, b: BMI): BMI {
            return if (a.height < b.height) a else b
        }

        fun maxHeight(a: BMI, b: BMI): BMI {
            return if (a.height > b.height) a else b
        }

        fun minWeight(a: BMI, b: BMI): BMI {
            return if (a.weight < b.weight) a else b
        }

        fun maxWeight(a: BMI, b: BMI): BMI {
            return if (a.weight > b.weight) a else b
        }

        fun minBMI(a: BMI, b: BMI): BMI {
            val bmiA = Utils.calculateBMI(a.weight,a.height)
            val bmiB = Utils.calculateBMI(a.weight,a.height)
            return if (bmiA < bmiB) a else b
        }

        fun maxBMI(a: BMI, b: BMI): BMI {
            val bmiA = Utils.calculateBMI(a.weight,a.height)
            val bmiB = Utils.calculateBMI(a.weight,a.height)
            return if (bmiA > bmiB) a else b
        }

    }


}