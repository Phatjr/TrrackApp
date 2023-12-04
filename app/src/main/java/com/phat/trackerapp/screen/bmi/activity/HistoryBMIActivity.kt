package com.phat.trackerapp.screen.bmi.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.phat.trackerapp.R
import com.phat.trackerapp.callback.OnItemListener
import com.phat.trackerapp.data.database.BMIDatabase
import com.phat.trackerapp.data.model.BMI
import com.phat.trackerapp.extension.executeAsyncTask
import com.phat.trackerapp.screen.bmi.adapter.BMIAdapter
import com.phat.trackerapp.screen.record.bmi.EditAddBMIActivity
import com.phat.trackerapp.utils.Constants
import kotlinx.android.synthetic.main.activity_history.btnBack
import kotlinx.android.synthetic.main.activity_history.layoutEmpty
import kotlinx.android.synthetic.main.activity_history.rvHistory
import kotlinx.coroutines.cancel

class HistoryBMIActivity : AppCompatActivity() , OnItemListener{
    private lateinit var mBMIs: ArrayList<BMI>

    private lateinit var mBMIAdapter: BMIAdapter

    private var mIntent: Intent? = null

    private var mIsUpdating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        initData()
        handleEvent()
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.cancel()
    }

    private fun initData() {
        mBMIs = ArrayList()
        loadData()
        mBMIAdapter = BMIAdapter(this, mBMIs, this)
        rvHistory.adapter = mBMIAdapter
    }

    private fun loadData(){
        lifecycleScope.executeAsyncTask(onPreExecute = {
            mBMIs.clear()
        }, doInBackground = {
            mBMIs.addAll(BMIDatabase.getInstance(this).BMIDao().getAll().reversed())
        }, onPostExecute = {
            mBMIAdapter.notifyDataSetChanged()
            if(mBMIs.size==0) {
                layoutEmpty.visibility = View.VISIBLE
            }
        })
    }

    private fun handleEvent() {
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

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            mIsUpdating = true
            loadData()
        }
    }

    private fun showScreen(intent: Intent){
        mIntent = null
        mIntent = intent
        resultLauncher.launch(mIntent)
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, EditAddBMIActivity::class.java)
        intent.putExtra(Constants.ID,  mBMIs[position].id)
        showScreen(intent)
    }

}