package com.phat.trackerapp.screen.bloodpressure.history.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.phat.trackerapp.R
import com.phat.trackerapp.callback.OnItemListener
import com.phat.trackerapp.data.database.TrackerDatabase
import com.phat.trackerapp.data.model.Tracker
import com.phat.trackerapp.extension.executeAsyncTask
import com.phat.trackerapp.screen.bloodpressure.history.adapter.HistoryAdapter
import com.phat.trackerapp.screen.record.tracker.EditTrackerActivity
import com.phat.trackerapp.utils.Constants
import kotlinx.android.synthetic.main.activity_history.btnBack
import kotlinx.android.synthetic.main.activity_history.layoutEmpty
import kotlinx.android.synthetic.main.activity_history.rvHistory
import kotlinx.coroutines.cancel

class HistoryActivity : AppCompatActivity(), OnItemListener{

    private lateinit var mTrackers: ArrayList<Tracker>

    private lateinit var mHistoryAdapter: HistoryAdapter

    private var mIntent: Intent? = null

    private var mIsUpdating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        // test
        val data = intent
        if(data != null && data.data != null){

            Toast.makeText(this ,"""
                | Name from URI :: ${data.getStringExtra("path")}
            """.trimMargin(), Toast.LENGTH_SHORT).show()
        }
        initData()
        handleEvents()
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.cancel()
    }

    override fun onItemClick(position: Int) {
        val tracker = mTrackers[position]
        val mIntent = Intent(this, EditTrackerActivity::class.java)
        mIntent.putExtra(Constants.ID, tracker.id)
        mIntent.putExtra(Constants.TYPE, Constants.HISTORY)
        showScreen(mIntent)
    }

    private fun showScreen(intent: Intent){
        mIntent = null
        mIntent = intent
        resultLauncher.launch(mIntent)
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            mIsUpdating = true
            loadData()
        }
    }

    private fun initData() {

        mTrackers = ArrayList()

        mHistoryAdapter = HistoryAdapter(this, mTrackers, this)
        rvHistory.adapter = mHistoryAdapter
        loadData()
    }

    private fun loadData(){
        lifecycleScope.executeAsyncTask(onPreExecute = {
            mTrackers.clear()
        }, doInBackground = {
            mTrackers.addAll(TrackerDatabase.getInstance(this).trackerDao().getAll().reversed())
        }, onPostExecute = {
            mHistoryAdapter.notifyDataSetChanged()
            if(mTrackers.size==0) {
                layoutEmpty.visibility = View.VISIBLE
            }
        })
    }

    private fun handleEvents() {
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(mIsUpdating){
                    setResult(RESULT_OK)
                }else{
                    setResult(RESULT_CANCELED)
                }

                finish()
            }
        })
    }

}