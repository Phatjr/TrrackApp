package com.phat.trackerapp.screen.record.bloodsugar

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.phat.trackerapp.R
import com.phat.trackerapp.callback.OnItemListener
import com.phat.trackerapp.data.dao.BloodSugarDao
import com.phat.trackerapp.data.database.BloodSugarDatabase
import com.phat.trackerapp.data.model.BloodSugar
import com.phat.trackerapp.database.SharePrefDB
import com.phat.trackerapp.extension.roundTo
import com.phat.trackerapp.extension.showDialogChooseDate
import com.phat.trackerapp.extension.showDialogChooseTime
import com.phat.trackerapp.extension.showNotice
import com.phat.trackerapp.screen.note.activity.EditAddNoteActivity
import com.phat.trackerapp.screen.record.adapter.ItemStateAdapter
import com.phat.trackerapp.screen.record.adapter.NanoTagClass
import com.phat.trackerapp.screen.record.adapter.TagNoteAdapter
import com.phat.trackerapp.utils.Constants
import com.phat.trackerapp.utils.Utils
import kotlinx.android.synthetic.main.activity_edit_add_blood_sugar.*
import kotlinx.android.synthetic.main.layout_date_time_option.*
import kotlinx.android.synthetic.main.layout_date_time_option.txtTime
import kotlinx.android.synthetic.main.layout_dialog_delete_tag.*
import kotlinx.android.synthetic.main.layout_dialog_info_state_blood_sugar.*
import kotlinx.android.synthetic.main.layout_item_alarm_reminder.*
import kotlinx.android.synthetic.main.layout_notes_dialog.*
import kotlinx.android.synthetic.main.layout_toolbar_edit_add_record.*
import kotlinx.android.synthetic.main.layout_toolbar_edit_add_record.txtTitle
import java.text.SimpleDateFormat
import java.util.*

class EditAddBloodSugarActivity : AppCompatActivity(), OnItemListener {
    private var mID: Int = -1

    private var mValue: Float = 75f

    private var mCurrentState: Int = 1

    private var mDialogNote: Dialog? = null

    private lateinit var mTagNoteAdapter: TagNoteAdapter

    private lateinit var mCurBloodSugar: BloodSugar

    private lateinit var mBloodSugarDao: BloodSugarDao

    private lateinit var mInfoDialog: Dialog

    private lateinit var mStates: ArrayList<Int>

    private lateinit var mItemStateAdapter: ItemStateAdapter

    private var mIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_add_blood_sugar)
        initData()
        initView()
        handleEvents()
    }

    private fun initView() {
        initDialog()
        if (mID == -1) {
            //add
            txtTitle.text = getString(R.string.txt_add_record)
            edtValue.setText((75f).toFloat().toString())
            tvState.setText("${getString(R.string.txt_state)}: ${getString(R.string.txt_default)}")
            resetUnitTop()
        } else {
            //edit
            tvState.setText(
                "${getString(R.string.txt_state)}: ${
                    getString(
                        BloodSugar.getTypeState(
                            mCurBloodSugar.typeState
                        )
                    )
                }"
            )
            txtTitle.text = getString(R.string.txt_add_record)
            btnDelete.visibility = View.VISIBLE
            mValue = mCurBloodSugar.value
            edtValue.setText(mValue.toString())
            edtValue.setSelection(edtValue.text.length)
            Log.d("222222", mCurBloodSugar.unit.toString())
            if (mCurBloodSugar.unit == 18f) {
                resetUnitTop()
                btnUnit.isUnitAbove = true
            } else {
                resetUnitBottom()
                btnUnit.isUnitAbove = false
            }
            setStateView()
        }
        initDateTime()
    }

    private fun initDialog() {
        mInfoDialog = Utils.onCreateDialog(this, R.layout.layout_dialog_info_state_blood_sugar)
        addStateData()
        mItemStateAdapter = ItemStateAdapter(this, mStates, this)
        mInfoDialog.rvStates.adapter = mItemStateAdapter

        mDialogNote = Utils.onCreateDialog(this, R.layout.layout_notes_dialog)
        mDialogNote?.window?.setGravity(Gravity.BOTTOM)

        val tags: ArrayList<String> = ArrayList()
        tags.addAll(
            SharePrefDB.getInstance(this).getAllNotes(Constants.KEY_BLOOD_SUGAR_NOTES).toList()
        )
        mTagNoteAdapter = TagNoteAdapter(this, tags)
        val nanoTagClass = NanoTagClass(this, mDialogNote?.rvTags!!, mTagNoteAdapter)
    }

    private fun initData() {
        mID = intent.getIntExtra(Constants.ID, -1)
        mBloodSugarDao = BloodSugarDatabase.getInstance(this).bloodSugarDao()

        mCurBloodSugar = mBloodSugarDao.findBloodSugarById(mID)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                mTagNoteAdapter.reloadTag(Constants.KEY_BLOOD_SUGAR_NOTES)
            } else {
                mDialogNote?.dismiss()
            }
        }

    @SuppressLint("SetTextI18n")
    private fun handleEvents() {
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setResult(RESULT_CANCELED)
                finish()
            }
        })

        mInfoDialog.btnClose.setOnClickListener {
            mInfoDialog.dismiss()
        }

        mInfoDialog.btnDone.setOnClickListener {
            tvState.setText("${getString(R.string.txt_state)}: ${getString(mStates[mItemStateAdapter.mPosSelected])}")
            mInfoDialog.dismiss()
        }

        btnSave.setOnClickListener {
            val id = if (mID == -1) 0 else mID
            val value = edtValue.text.toString()
            if (value.isNotEmpty()) {
                val tag = mTagNoteAdapter.getAllTagSelected()
                if (isInvalidNumber(value)) {
                    handleInvalidValue()
                } else {
                    if (value.toFloat() != mValue) {
                        mValue = value.toFloat()
                    }
                    val bloodSugar = BloodSugar(
                        id = id,
                        value = mValue,
                        unit = btnUnit.unitValue,
                        state = mCurrentState,
                        date = txtDate.text.toString(),
                        time = txtTime.text.toString(),
                        typeState = mStates[mItemStateAdapter.mPosSelected],
                        tag = tag
                    )
                    Log.d("444444",getString(mStates[mItemStateAdapter.mPosSelected]))
                    if (mID != -1 && mCurBloodSugar != null) {
                        mBloodSugarDao.update(bloodSugar)
                    } else {
                        mBloodSugarDao.insert(bloodSugar)
                    }

                    gotoBeforeScreen()
                }
            } else {
                showNotice(getString(R.string.txt_please_enter_content))
            }
        }

        btnDate.setOnClickListener {
            showDialogChooseDate(txtDate)
        }

        btnTime.setOnClickListener {
            showDialogChooseTime(txtTime)
        }

        btnUnit.setOnClickListener {
            btnUnit.isUnitAbove = !btnUnit.isUnitAbove
            Log.d("66666", mValue.toString() +"    ")
            if (btnUnit.isUnitAbove) {
                resetUnitTop()
                mValue *= 18f
                edtValue.setText(mValue.roundTo(1).toString())
            } else {
                resetUnitBottom()
                mValue /= 18f
                edtValue.setText(mValue.roundTo(1).toString())
            }
            Log.d("66666", mValue.roundTo(1).toString())
            edtValue.setSelection(edtValue.text.length)
        }

        layoutAddNote.setOnClickListener {
            mDialogNote?.show()
        }

        layoutStateDetail.setOnClickListener {
            mInfoDialog.show()
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
                    if (mCurBloodSugar != null) {

                        mDialogDeleteNote.dismiss()
                        mBloodSugarDao.delete(mCurBloodSugar)
                        gotoBeforeScreen()
                    }
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
            mDialogDeleteNote.show()
        }

        edtValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }


            override fun afterTextChanged(s: Editable?) {
                Log.d("55555","afterTextChanged")
                if (!s.toString().isNullOrEmpty() && !isInvalidNumber(s.toString())) {
                    setStateView()
                }
            }

        })

        edtValue.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode === KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                Log.d("22222", "fffff")
                val text = edtValue.text.toString()
                if (text.isNullOrEmpty()) {
                    handleInvalidValue()
                } else if (isInvalidNumber(text)) {
                    handleInvalidValue()
                } else {
                    setStateView()
                }
            }
            false
        })

        mDialogNote?.apply {
            btnCloseBpNote?.setOnClickListener {
                dismiss()
            }

            btnEditAddNote?.setOnClickListener {

                val intent = Intent(this@EditAddBloodSugarActivity, EditAddNoteActivity::class.java)
                intent.putExtra(Constants.KEY_NOTES, Constants.KEY_BLOOD_SUGAR_NOTES)
                showScreen(intent)
            }

            btnSaveNote?.setOnClickListener {

                dismiss()
            }
        }
    }

    private fun addStateData() {
        mStates = ArrayList()
        mStates.add(R.string.txt_default)
        mStates.add(R.string.txt_during_fasting)
        mStates.add(R.string.txt_before_eating)
        mStates.add(R.string.txt_after_eating_1h)
        mStates.add(R.string.txt_after_eating_2h)
        mStates.add(R.string.txt_before_bedtime)
        mStates.add(R.string.txt_before_workout)
        mStates.add(R.string.txt_after_wordkout)
    }

    private fun isInvalidNumber(value: String): Boolean {
        Log.d("55555",(value.toFloatOrNull() == null).toString())
        if (value.toFloatOrNull() == null) return true
        return value.toFloat() > (35 * btnUnit.unitValue) || (value.toFloat() < (1 * btnUnit.unitValue))
    }

    private fun showScreen(intent: Intent) {
        mIntent = null
        mIntent = intent
        resultLauncher.launch(mIntent)
    }

    private fun gotoBeforeScreen() {
        setResult(RESULT_OK)
        finish()
    }

    private fun handleInvalidValue() {
        if (btnUnit.isUnitAbove) {
            edtValue.setText((75f).toFloat().toString())
            showNotice(getString(R.string.txt_please_enter_valid_blood_sugar_2))
        } else {
            edtValue.setText((4f).toFloat().toString())
            showNotice(getString(R.string.txt_please_enter_valid_blood_sugar_1))
        }
        edtValue.setSelection(edtValue.text.length)
        mValue = edtValue.text.toString().toFloat()
        setStateView()
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

    private fun setStateView() {
        resetArrow()
        try {
            mValue = edtValue.text.toString().toFloat()
        } catch (e: java.lang.NumberFormatException) {
            e.printStackTrace()
        }
        Log.d("444444", mValue.toString() + "     " + btnUnit.unitValue)
        when (mValue) {
            in 1.0 * btnUnit.unitValue..3.9 * btnUnit.unitValue -> {
                btnLow.visible = true
                tvStateInfo.background.setColorFilter(btnLow.color, PorterDuff.Mode.SRC_IN)
                tvStateInfo.setText(btnLow.state)
                mCurrentState = 0
            }
            in 4.0 * btnUnit.unitValue..7.7 * btnUnit.unitValue -> {
                btnNormal.visible = true
                tvStateInfo.background.setColorFilter(btnNormal.color, PorterDuff.Mode.SRC_IN)
                tvStateInfo.setText(btnNormal.state)
                mCurrentState = 1
            }
            in 7.8 * btnUnit.unitValue..8.4 * btnUnit.unitValue -> {
                btnPreDiabetes.visible = true
                tvStateInfo.background.setColorFilter(btnPreDiabetes.color, PorterDuff.Mode.SRC_IN)
                tvStateInfo.setText(btnPreDiabetes.state)
                mCurrentState = 2
            }
            in 8.5 * btnUnit.unitValue..35.0 * btnUnit.unitValue -> {
                btnDiabetes.visible = true
                tvStateInfo.background.setColorFilter(btnDiabetes.color, PorterDuff.Mode.SRC_IN)
                tvStateInfo.setText(btnDiabetes.state)
                mCurrentState = 3
            }
        }
    }

    private fun resetArrow() {
        btnNormal.visible = false
        btnLow.visible = false
        btnPreDiabetes.visible = false
        btnDiabetes.visible = false
    }

    private fun resetUnitBottom() {
        btnLow.valueRange = "<4.0"
        btnNormal.valueRange = "4.0~7.7"
        btnPreDiabetes.valueRange = "7.7~8.4"
        btnDiabetes.valueRange = ">=8.5"
    }

    private fun resetUnitTop() {
        btnLow.valueRange = "<72"
        btnNormal.valueRange = "72~139"
        btnPreDiabetes.valueRange = "140~152"
        btnDiabetes.valueRange = ">=153"
    }

    override fun onItemClick(position: Int) {
        mItemStateAdapter.mPosSelected = position
    }

}