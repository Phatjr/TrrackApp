package com.phat.trackerapp.screen.bloodsugar

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.horen.chart.barchart.IBarData
import com.phat.trackerapp.R
import com.phat.trackerapp.callback.OnItemListener
import com.phat.trackerapp.data.database.BloodSugarDatabase
import com.phat.trackerapp.data.model.BloodSugar
import com.phat.trackerapp.extension.executeAsyncTask
import com.phat.trackerapp.extension.onCreateBarChartOneColumn
import com.phat.trackerapp.screen.bloodsugar.adapter.BloodSugarAdapter
import com.phat.trackerapp.screen.home.model.TrackerBarData
import com.phat.trackerapp.screen.record.bloodsugar.EditAddBloodSugarActivity
import com.phat.trackerapp.utils.Constants
import com.phat.trackerapp.utils.Utils
import kotlinx.android.synthetic.main.activity_blood_sugar.*
import kotlinx.android.synthetic.main.layout_dialog_add_new_record.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.coroutines.cancel

class BloodSugarActivity : AppCompatActivity(), OnItemListener{
    private lateinit var mAddNewRecordDialog: Dialog

    private lateinit var mBloodSugars: ArrayList<BloodSugar>

    private lateinit var mLastBloodSugars: ArrayList<BloodSugar>

    private lateinit var mBloodSugarAdapter: BloodSugarAdapter

    private lateinit var mLatestBloodSugar: BloodSugar

    private var mIntent: Intent? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blood_sugar)
        initData()
        handleEvent()
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.cancel()
    }

    private fun initData() {
        txtTitle.text = getString(R.string.txt_blood_sugar)
        btnHistory.visibility = View.INVISIBLE

        mAddNewRecordDialog = Utils.onCreateDialog(this, R.layout.layout_dialog_add_new_record)
        mBloodSugars = ArrayList()
        mLastBloodSugars = ArrayList()

        loadData()

        mBloodSugarAdapter = BloodSugarAdapter(this, mLastBloodSugars, this)
        rvLastBloodSugar.adapter = mBloodSugarAdapter
    }

    private fun loadData(){
        lifecycleScope.executeAsyncTask(onPreExecute = {
            mBloodSugars.clear()
            mLastBloodSugars.clear()
        }, doInBackground = {
            BloodSugarDatabase.getInstance(this).bloodSugarDao().getAll()
        }, onPostExecute = {
            mBloodSugarAdapter.notifyDataSetChanged()
            val bloodSugarList = it
            if (bloodSugarList != null && bloodSugarList.isNotEmpty()) {
                txtNoChart.visibility = View.GONE
                chartBloodSugar.visibility = View.VISIBLE
                layoutEmpty.visibility = View.GONE
                mBloodSugars.addAll(bloodSugarList)
                mLatestBloodSugar = mBloodSugars[mBloodSugars.size - 1]
                if (mBloodSugars.size >= 3) {
                    mLastBloodSugars.add(mBloodSugars[mBloodSugars.size - 1])
                    mLastBloodSugars.add(mBloodSugars[mBloodSugars.size - 2])
                    mLastBloodSugars.add(mBloodSugars[mBloodSugars.size - 3])
                } else if (mBloodSugars.size >= 2) {
                    mLastBloodSugars.add(mBloodSugars[mBloodSugars.size - 1])
                    mLastBloodSugars.add(mBloodSugars[mBloodSugars.size - 2])
                } else if (mBloodSugars.size > 0) {
                    mLastBloodSugars.add(mBloodSugars[mBloodSugars.size - 1])
                } else {

                }
                mBloodSugars.sortBy { it.getTimeLong() }
            } else {
                txtNoChart.visibility = View.VISIBLE
                chartBloodSugar.visibility = View.INVISIBLE
                layoutEmpty.visibility = View.VISIBLE
                mAddNewRecordDialog.show()
            }
            // chart

            val data: ArrayList<IBarData> = ArrayList()

            for (i in 0 until mBloodSugars.size) {
                val bloodSugar = mBloodSugars[i]
                data.add(
                    TrackerBarData(
                        bloodSugar.time + ", " + bloodSugar.date,
                        bloodSugar.value.toFloat()
                    )
                )
            }

            val xDays = ArrayList<String>()
            xDays.add(getString(R.string.txt_blood_sugar))

            onCreateBarChartOneColumn(data,xDays,chartBloodSugar)
        })
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("2222222","ggggg")
            loadData()
        }
    }

    private fun handleEvent() {
        btnBack.setOnClickListener {
            finish()
        }

        btnAdd.setOnClickListener {
            val intent = Intent(this, EditAddBloodSugarActivity::class.java)
            showScreen(intent)
        }

        mAddNewRecordDialog.apply {
            btnAddRecord.setOnClickListener {
                dismiss()
                val intent = Intent(this@BloodSugarActivity, EditAddBloodSugarActivity::class.java)
                showScreen(intent)
            }

            btnClose.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun showScreen(intent: Intent){
        mIntent = null
        mIntent = intent
        resultLauncher.launch(mIntent)
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this@BloodSugarActivity, EditAddBloodSugarActivity::class.java)
        intent.putExtra(Constants.ID, mLastBloodSugars[position].id)
        showScreen(intent)
    }

}