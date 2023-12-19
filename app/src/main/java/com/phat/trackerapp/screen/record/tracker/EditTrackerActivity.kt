package com.phat.trackerapp.screen.record.tracker

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.phat.trackerapp.R
import com.phat.trackerapp.data.database.TrackerDatabase
import com.phat.trackerapp.data.model.Tracker
import com.phat.trackerapp.database.SharePrefDB
import com.phat.trackerapp.extension.showDialogChooseDate
import com.phat.trackerapp.extension.showDialogChooseTime
import com.phat.trackerapp.screen.note.activity.EditAddNoteActivity
import com.phat.trackerapp.screen.record.adapter.NanoTagClass
import com.phat.trackerapp.screen.record.adapter.TagNoteAdapter
import com.phat.trackerapp.utils.Constants
import com.phat.trackerapp.utils.Utils
import kotlinx.android.synthetic.main.activity_edit_tracker.btnSave
import kotlinx.android.synthetic.main.activity_edit_tracker.imgStatus
import kotlinx.android.synthetic.main.activity_edit_tracker.layoutTypeInfo
import kotlinx.android.synthetic.main.activity_edit_tracker.pickerDiastolic
import kotlinx.android.synthetic.main.activity_edit_tracker.pickerPulse
import kotlinx.android.synthetic.main.activity_edit_tracker.pickerSystolic
import kotlinx.android.synthetic.main.activity_edit_tracker.txtLimited
import kotlinx.android.synthetic.main.activity_edit_tracker.txtTypeInfo
import kotlinx.android.synthetic.main.layout_date_time_option.btnDate
import kotlinx.android.synthetic.main.layout_date_time_option.btnTime
import kotlinx.android.synthetic.main.layout_date_time_option.layoutAddNote
import kotlinx.android.synthetic.main.layout_date_time_option.txtDate
import kotlinx.android.synthetic.main.layout_date_time_option.txtTime
import kotlinx.android.synthetic.main.layout_dialog_blood_pressure_type.btnGoIt
import kotlinx.android.synthetic.main.layout_dialog_delete_tag.btnCancelDelete
import kotlinx.android.synthetic.main.layout_dialog_delete_tag.btnDeleteNote
import kotlinx.android.synthetic.main.layout_dialog_delete_tag.txtContent
import kotlinx.android.synthetic.main.layout_dialog_delete_tag.txtTitle
import kotlinx.android.synthetic.main.layout_notes_dialog.btnCloseBpNote
import kotlinx.android.synthetic.main.layout_notes_dialog.btnEditAddNote
import kotlinx.android.synthetic.main.layout_notes_dialog.btnSaveNote
import kotlinx.android.synthetic.main.layout_notes_dialog.rvTags
import kotlinx.android.synthetic.main.layout_toolbar_edit_add_record.btnBack
import kotlinx.android.synthetic.main.layout_toolbar_edit_add_record.btnDelete

class EditTrackerActivity : AppCompatActivity() {

    private var mDialogInfo: Dialog? = null

    private var mDialogNote: Dialog? = null

    private lateinit var mTagNoteAdapter: TagNoteAdapter

    private var mTracker: Tracker? = null

    private var mType: Int = 0

    private var mIntent: Intent? = null

    private var mIsBackScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_tracker)
        initData()
        handleEvents()
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            mTagNoteAdapter.reloadTag(Constants.KEY_CHIP_NOTES)
        }else{
            mDialogNote?.dismiss()
        }
    }

    private fun initData() {
        btnDelete.visibility = View.VISIBLE
        txtTitle.setText(R.string.txt_edit_record)
        val id = intent.getIntExtra(Constants.ID, 0)
        mType = intent.getIntExtra(Constants.TYPE, Constants.HOME)
        mTracker = TrackerDatabase.getInstance(this).trackerDao().findTrackerById(id) //todo dựa theo id để lấy data ra


        //todo gán data để hiển thị ra màn hình
        pickerSystolic.minValue = 20
        pickerSystolic.maxValue = 300

        if (mTracker!= null && mTracker?.systolic != null) {
            pickerSystolic.value = mTracker?.systolic!!
        }

        pickerDiastolic.minValue = 20
        pickerDiastolic.maxValue = 300
        if (mTracker!= null && mTracker?.diastolic != null) {
            pickerDiastolic.value = mTracker?.diastolic!!
        }

        pickerPulse.minValue = 20
        pickerPulse.maxValue = 200
        if (mTracker!= null && mTracker?.pulse != null) {
            pickerPulse.value = mTracker?.pulse!!
        }

        pickerSystolic.wrapSelectorWheel = false
        pickerDiastolic.wrapSelectorWheel = false
        pickerPulse.wrapSelectorWheel = false

        updateUI()

        initDateTime()

        mDialogInfo = Utils.onCreateDialog(this,R.layout.layout_dialog_blood_pressure_type)

        mDialogNote = Utils.onCreateDialog(this, R.layout.layout_notes_dialog)
        mDialogNote?.window?.setGravity(Gravity.BOTTOM)
        val tags: ArrayList<String> = ArrayList()
        tags.addAll(
            SharePrefDB.getInstance(this).getAllNotes(Constants.KEY_CHIP_NOTES).toList()
        )
        mTagNoteAdapter = TagNoteAdapter(this, tags)
        val tagsSelected = mTracker?.tag?.split("#")
        tagsSelected?.forEach {
            if (!it.trim().equals("")) {
                mTagNoteAdapter.tagSelected.add(it)
            }
        }
        val nanoTagClass = NanoTagClass(this, mDialogNote?.rvTags!!, mTagNoteAdapter)
    }

    private fun handleEvents() {
        btnBack.setOnClickListener {
            mIsBackScreen = true
            showScreen()
        }

        onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setResult(RESULT_CANCELED)
                finish()
            }
        })

        layoutTypeInfo.setOnClickListener {
            mDialogInfo?.show()
        }

        mDialogInfo?.let {
            it.btnGoIt.setOnClickListener {
                mDialogInfo?.dismiss()
            }
        }

        pickerSystolic.setOnValueChangedListener { _, _, _ ->
            updateUI()
        }

        pickerDiastolic.setOnValueChangedListener { _, _, _ ->
            updateUI()
        }

        pickerPulse.setOnValueChangedListener { _, _, _ ->
        }

        btnDate.setOnClickListener {
            showDialogChooseDate(txtDate)
        }

        btnTime.setOnClickListener {
            showDialogChooseTime(txtTime)
        }

        layoutAddNote.setOnClickListener {
            mDialogNote?.show()
        }

        mDialogNote?.btnCloseBpNote?.setOnClickListener {
            mDialogNote?.dismiss()
        }

        mDialogNote?.btnEditAddNote?.setOnClickListener {

            val intent = Intent(this, EditAddNoteActivity::class.java)
            showScreen(intent)
        }

        mDialogNote?.btnSaveNote?.setOnClickListener {
            mDialogNote?.dismiss()
        }

        btnSave.setOnClickListener {
            val state = txtTypeInfo.text.toString().trim()
            val systolic = pickerSystolic.value
            val diastolic = pickerDiastolic.value
            val pulse = pickerPulse.value
            val date = txtDate.text.toString()
            val time = txtTime.text.toString()
            val tag = mTagNoteAdapter.getAllTagSelected()

            val tracker =
                Tracker(mTracker?.id!!, time, date, systolic, diastolic, pulse, state, tag)
            TrackerDatabase.getInstance(this).trackerDao().update(tracker)
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
                try {
                    if(mTracker != null){
                        mDialogDeleteNote.dismiss()
                        TrackerDatabase.getInstance(this).trackerDao().delete(mTracker!!)
                        gotoBeforeScreen()
                    }
                }catch (e: NullPointerException){
                    e.printStackTrace()
                }
            }
            mDialogDeleteNote.show()
        }
    }

    private fun showScreen(intent: Intent? = null) {
        mIntent = null
        mIntent = intent
        if(mIsBackScreen)   onBackPressedDispatcher.onBackPressed()
        else{
            resultLauncher.launch(mIntent)
        }
    }

    private fun gotoBeforeScreen() {
        setResult(RESULT_OK)
        finish()
    }

    private fun updateUI() {
        val valueSystolic = pickerSystolic.value
        val valueDiastolic = pickerDiastolic.value

        if (valueSystolic in 0..119) {
            if (valueDiastolic in 0..79) {
                imgStatus.setImageResource(R.drawable.ic_status_1)
                txtTypeInfo.text = getString(R.string.txt_normal_blood_pressure)
                txtLimited.text = getString(R.string.txt_blood_level_1)
                layoutTypeInfo.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#00C57E"))
            }
        }

        if (valueSystolic in 120..129) {
            if (valueDiastolic in 0..79) {
                imgStatus.setImageResource(R.drawable.ic_status_2)
                txtTypeInfo.text = getString(R.string.txt_elevated_blood_pressure)
                txtLimited.text = getString(R.string.txt_blood_level_2)
                layoutTypeInfo.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#E9D841"))
            }
        }

        val z2 = valueSystolic in 130..139
        if (z2 || valueDiastolic in 80..89) {
            imgStatus.setImageResource(R.drawable.ic_status_3)
            txtTypeInfo.text = getString(R.string.txt_high_blood_pressure_stage_1)
            txtLimited.text = getString(R.string.txt_blood_level_3)
            layoutTypeInfo.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F7B11E"))
        }

        val z3 = pickerSystolic.value in 140..180
        if (z3 || pickerDiastolic.value in 90..120) {
            imgStatus.setImageResource(R.drawable.ic_status_4)
            txtTypeInfo.text = getString(R.string.txt_high_blood_pressure_stage_2)
            txtLimited.text = getString(R.string.txt_blood_level_4)
            layoutTypeInfo.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF6B00"))
        }

        if (pickerSystolic.value > 180 || pickerDiastolic.value > 120) {
            imgStatus.setImageResource(R.drawable.ic_status_5)
            txtTypeInfo.text = getString(R.string.txt_diagnose_level_5)
            txtLimited.text = getString(R.string.txt_blood_level_5)
            layoutTypeInfo.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D72626"))
        }
    }

    private fun initDateTime() {
        if(mTracker != null) {
            txtDate.text = mTracker?.date
            txtTime.text = mTracker?.time
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDialogInfo = null
    }

}