package com.phat.trackerapp.screen.record.heartrate

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.phat.trackerapp.R
import com.phat.trackerapp.data.dao.HeartRateDao
import com.phat.trackerapp.data.database.HeartRateDatabase
import com.phat.trackerapp.database.SharePrefDB
import com.phat.trackerapp.extension.showDialogChooseDate
import com.phat.trackerapp.extension.showDialogChooseTime
import com.phat.trackerapp.screen.heartrate.HeartRateActivity
import com.phat.trackerapp.screen.note.activity.EditAddNoteActivity
import com.phat.trackerapp.screen.record.adapter.NanoTagClass
import com.phat.trackerapp.screen.record.adapter.TagNoteAdapter
import com.phat.trackerapp.utils.Constants
import com.phat.trackerapp.utils.Utils
import com.phat.trackerapp.utils.measuring.model.HeartRate
import kotlinx.android.synthetic.main.activity_edit_add_heart_rate.*
import kotlinx.android.synthetic.main.layout_date_time_option.*
import kotlinx.android.synthetic.main.layout_dialog_delete_tag.*
import kotlinx.android.synthetic.main.layout_notes_dialog.*
import kotlinx.android.synthetic.main.layout_toolbar_edit_add_record.*
import kotlinx.android.synthetic.main.layout_toolbar_edit_add_record.txtTitle
import java.text.SimpleDateFormat
import java.util.*

class EditAddHeartRateActivity : AppCompatActivity() {
    private var mID: Int = 0

    private var mDialogNote: Dialog? = null

    private lateinit var mTagNoteAdapter: TagNoteAdapter

    private lateinit var mCurrentHeartRate: HeartRate

    private var mIntent: Intent? = null

    private lateinit var mHeartRateDao: HeartRateDao

    private var mTrackerValue: Int = 0

    private var mIsFromTracker: Boolean = false

    private var mIsBackScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_add_heart_rate)
        Utils.changeStatusBarColor(this, R.color.red)
        initData()
        initView()
        handleEvents()
    }

    private fun initData() {
        mHeartRateDao = HeartRateDatabase.getInstance(this).heartRateDao()
        mID = intent.getIntExtra(Constants.ID, -1)
        mTrackerValue = intent.getIntExtra(Constants.VALUE, 0)
        mIsFromTracker = intent.getBooleanExtra(Constants.FROM_TRACKER, false)
        mCurrentHeartRate = mHeartRateDao.findBMIById(mID)
    }

    private fun initView() {
        relativeLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.red))

        if (mID != -1 && mCurrentHeartRate != null) {
            //edit
            txtTitle.setText(R.string.txt_edit_record)
            btnDelete.visibility = View.VISIBLE

            numberPicker.value = mCurrentHeartRate.heartRate
            applyValueIntoView(numberPicker.value)

        } else {
            //add
            txtTitle.setText(R.string.txt_add_record)
            if (mTrackerValue > 0) {
                numberPicker.value = mTrackerValue
                applyValueIntoView(mTrackerValue)
            }
        }

        initDateTime()

        mDialogNote = Utils.onCreateDialog(this, R.layout.layout_notes_dialog)
        mDialogNote?.window?.setGravity(Gravity.BOTTOM)

        val tags: ArrayList<String> = ArrayList()
        tags.addAll(
            SharePrefDB.getInstance(this).getAllNotes(Constants.KEY_BMI_NOTES).toList()
        )
        mTagNoteAdapter = TagNoteAdapter(this, tags)
        val nanoTagClass = NanoTagClass(this, mDialogNote?.rvTags!!, mTagNoteAdapter)
    }

    private fun initDateTime() {
        val date = Date()
        val formatDate = SimpleDateFormat("dd/MM/yyyy")
        val formatTime = SimpleDateFormat("HH:mm")
        val strDate = formatDate.format(date)
        val strTime = formatTime.format(date)
        txtDate.text = strDate
        txtTime.text = strTime
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                mTagNoteAdapter.reloadTag(Constants.KEY_BMI_NOTES)
            } else {
                mDialogNote?.dismiss()
            }
        }

    private fun handleEvents() {
        btnBack.setOnClickListener {
            if (mIsFromTracker) {
                gotoHeartRateScreen()
            } else {
                mIsBackScreen = true
                showScreen()
            }
        }

        numberPicker.setOnValueChangedListener { picker, oldVal, newVal -> applyValueIntoView(newVal) }

        layoutAddNote.setOnClickListener {
            mDialogNote?.show()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setResult(RESULT_CANCELED)
                finish()
            }
        })

        btnSave.setOnClickListener {
            val id = if (mID == -1) 0 else mID
            val tag = mTagNoteAdapter.getAllTagSelected()
            val heartRate = HeartRate(
                id = id,
                heartRate = numberPicker.value,
                date = txtDate.text.toString(),
                time = txtTime.text.toString(),
                tag = tag
            )
            if (mID != -1 && mCurrentHeartRate != null) {
                mHeartRateDao.update(heartRate)
            } else {
                mHeartRateDao.insert(heartRate)
            }

            gotoBeforeScreen()
        }

        btnDelete.setOnClickListener {
            val mDialogDeleteNote = Utils.onCreateDialog(this, R.layout.layout_dialog_delete_tag)
            mDialogDeleteNote.txtTitle.text = getString(R.string.txt_delete_confirm)
            mDialogDeleteNote.txtContent.text =
                getString(R.string.txt_are_you_sure_to_delete_this_record)

            mDialogDeleteNote.btnCancelDelete.setOnClickListener {
                mDialogDeleteNote.dismiss()
            }

            mDialogDeleteNote.btnDeleteNote.setOnClickListener {
                if (mCurrentHeartRate != null) {

                    mDialogDeleteNote.dismiss()
                    mHeartRateDao.delete(mCurrentHeartRate)
                    gotoBeforeScreen()
                }
            }
            mDialogDeleteNote.show()
        }

        mDialogNote?.apply {
            btnCloseBpNote?.setOnClickListener {
                dismiss()
            }

            btnEditAddNote?.setOnClickListener {

                val intent = Intent(this@EditAddHeartRateActivity, EditAddNoteActivity::class.java)
                intent.putExtra(Constants.KEY_NOTES, Constants.KEY_BMI_NOTES)
                showScreen(intent)
            }

            btnSaveNote?.setOnClickListener {

                dismiss()
            }
        }

        btnDate.setOnClickListener {
            showDialogChooseDate(txtDate)
        }

        btnTime.setOnClickListener {
            showDialogChooseTime(txtTime)
        }
    }

    private fun gotoBeforeScreen() {
        if (mIsBackScreen) {
            onBackPressedDispatcher.onBackPressed()
        } else if (mIsFromTracker) {
            gotoHeartRateScreen()
        } else {
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun gotoHeartRateScreen() {
        val intent = Intent(this, HeartRateActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showScreen(intent: Intent? = null) {
        mIntent = null
        mIntent = intent
        if(mIsBackScreen){
            finish()
        }else{
            resultLauncher.launch(mIntent)
        }
    }

    private fun applyValueIntoView(newVal: Int) {
        if (newVal in 40..59) {
            sbState.progress = 5
            val purple: Int = ContextCompat.getColor(this, R.color.purple)
            layoutTypeInfo.backgroundTintList = ColorStateList.valueOf(purple)
            numberPicker?.selectedTextColor = purple
            txtTypeInfo.setText(R.string.txt_slow)
            tvDesBpm.text = getString(R.string.txt_resting_hearte_rate) + " <60 BPM"
        } else if (newVal in 60..100) {
            sbState.progress = 15
            val green: Int = ContextCompat.getColor(this, R.color.green)
            numberPicker?.selectedTextColor = green
            layoutTypeInfo.backgroundTintList = ColorStateList.valueOf(green)
            txtTypeInfo.setText(R.string.txt_normal)
            tvDesBpm.text = getString(R.string.txt_resting_hearte_rate) + " 60-100 BPM"
        } else {
            sbState.progress = 26
            val red: Int = ContextCompat.getColor(this, R.color.red)
            numberPicker?.selectedTextColor = red
            layoutTypeInfo.backgroundTintList = ColorStateList.valueOf(red)
            txtTypeInfo.setText(R.string.txt_fast)
            tvDesBpm.text = getString(R.string.txt_resting_hearte_rate) + " >100 BPM"
        }
    }


}