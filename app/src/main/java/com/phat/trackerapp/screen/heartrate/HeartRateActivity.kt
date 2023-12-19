package com.phat.trackerapp.screen.heartrate

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.phat.trackerapp.R
import com.phat.trackerapp.callback.OnItemListener
import com.phat.trackerapp.data.database.HeartRateDatabase
import com.phat.trackerapp.extension.executeAsyncTask
import com.phat.trackerapp.extension.setColorFilter
import com.phat.trackerapp.extension.shareCsvFile
import com.phat.trackerapp.screen.heartrate.adapter.HeartRateAdapter
import com.phat.trackerapp.screen.heartrate.chart.ChartActivity
import com.phat.trackerapp.screen.record.heartrate.EditAddHeartRateActivity
import com.phat.trackerapp.utils.Constants
import com.phat.trackerapp.utils.Utils
import com.phat.trackerapp.data.model.HeartRate
import kotlinx.android.synthetic.main.activity_heart_rate.btnAddData
import kotlinx.android.synthetic.main.activity_heart_rate.btnBack
import kotlinx.android.synthetic.main.activity_heart_rate.btnExport
import kotlinx.android.synthetic.main.activity_heart_rate.btnOpenChart
import kotlinx.android.synthetic.main.activity_heart_rate.btnTrackerNow
import kotlinx.android.synthetic.main.activity_heart_rate.ivIconHeart
import kotlinx.android.synthetic.main.activity_heart_rate.layoutEmpty
import kotlinx.android.synthetic.main.activity_heart_rate.rvHeartRate
import kotlinx.android.synthetic.main.activity_heart_rate.txtTitle
import kotlinx.android.synthetic.main.layout_dialog_add_new_record.btnAddRecord
import kotlinx.android.synthetic.main.layout_dialog_add_new_record.btnClose
import kotlinx.android.synthetic.main.layout_dialog_add_new_record.imgIcon
import java.io.File

class HeartRateActivity : AppCompatActivity(), OnItemListener {
    private lateinit var mHeartRateAdapter: HeartRateAdapter

    private lateinit var mList: ArrayList<HeartRate>

    private var mPosSelected: Int = -1

    private var mIntent: Intent? = null

    private var isGotoMeasureScreen: Boolean = false

    private lateinit var mAddNewRecordDialog: Dialog

    private var mIsBackScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heart_rate)
        Utils.changeStatusBarColor(this, R.color.red)
        initView()
        handleEvent()
    }


    private fun initDialog(){
        mAddNewRecordDialog = Utils.onCreateDialog(this, R.layout.layout_dialog_add_new_record)
        mAddNewRecordDialog.apply {
            imgIcon.setColorFilter(ContextCompat.getColor(this@HeartRateActivity,R.color.red))
            btnAddRecord.setColorFilter(ContextCompat.getColor(this@HeartRateActivity,R.color.red))
        }
    }

    private fun initView() {
        if(Utils.isEnglishLanguage()){
            txtTitle.text = Constants.HEART_RATE
        }
        initDialog()
        mList = ArrayList()
        rvHeartRate.layoutManager = LinearLayoutManager(this)
        mHeartRateAdapter = HeartRateAdapter(this, mList, this)
        rvHeartRate.adapter = mHeartRateAdapter

        loadData()

        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_out_shop)
        ivIconHeart.startAnimation(animation)

    }

    private fun loadData(){
        lifecycleScope.executeAsyncTask(onPreExecute = {
            mList.clear()
        }, doInBackground = {
            //Todo lay data heart rate tu local ra va gan gia tri vao mList
            mList.addAll(HeartRateDatabase.getInstance(this).heartRateDao().getAll())
        }, onPostExecute = {
            mHeartRateAdapter.notifyDataSetChanged()
            showEmptyLayout()
        })
    }

    private fun showEmptyLayout() {
        if (mList.isEmpty()) {
            mAddNewRecordDialog.show()
            layoutEmpty.visibility = View.VISIBLE
            rvHeartRate.visibility = View.GONE
        } else {
            layoutEmpty.visibility = View.GONE
            rvHeartRate.visibility = View.VISIBLE
        }
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadData()
        }
    }

    private fun showScreen(intent: Intent? = null) {
        mIntent = null
        mIntent = intent
        if(mIsBackScreen){
            finish()
        }else{
            gotoNextScreen()
        }
    }

    private fun gotoNextScreen() {
        if (isGotoMeasureScreen) {
            isGotoMeasureScreen = false
            startActivity(mIntent)
            finish()
        }else{
            resultLauncher.launch(mIntent)
        }
    }

    private fun handleEvent() {
        btnTrackerNow.setOnClickListener {

            val mIntent = Intent(this, MeasureActivity::class.java)
            isGotoMeasureScreen = true
            showScreen(mIntent)
        }

        ivIconHeart.setOnClickListener {

            val mIntent = Intent(this, MeasureActivity::class.java)
            isGotoMeasureScreen = true
            showScreen(mIntent)
        }

        btnExport.setOnClickListener {
            shareHistoryCsv()
        }

        btnBack.setOnClickListener {
            mIsBackScreen = true
            showScreen()
        }

        btnAddData.setOnClickListener {
            val mIntent = Intent(this, EditAddHeartRateActivity::class.java)
            showScreen(mIntent)
        }

        btnOpenChart.setOnClickListener {

            val mIntent = Intent(this, ChartActivity::class.java)
            showScreen(mIntent)
        }

        mAddNewRecordDialog.apply {
            btnClose.setOnClickListener {
                dismiss()
            }

            btnAddRecord.setOnClickListener {
                dismiss()
                val mIntent = Intent(this@HeartRateActivity, EditAddHeartRateActivity::class.java)
                showScreen(mIntent)
            }
        }

        mHeartRateAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                // Do nothing
                showEmptyLayout()
            }
        })
    }

    private fun shareHistoryCsv() {
        val mDir =
            File(this.getExternalFilesDir(null)?.absoluteFile?.absolutePath.toString() + "/" + Constants.DB_NAME)
        mDir.mkdir()

        val path: String = mDir.absolutePath + "/MyHeartRate.csv"

        csvWriter().open(path) {
            // Header
            writeRow(
                listOf(
                    "Date",
                    "Heart Rate",
                    "Tag"
                )
            )

            mList.forEach { heartRate ->
                writeRow(
                    listOf(
                        heartRate.time +", "+heartRate.date,
                        heartRate.heartRate,
                        heartRate.tag,
                    )
                )
            }
        }

        shareCsvFile(path)
    }

    override fun onItemClick(position: Int) {

        val intent = Intent(this, EditAddHeartRateActivity::class.java)
        intent.putExtra(Constants.ID, mList[position].id)
        showScreen(intent)
    }


}