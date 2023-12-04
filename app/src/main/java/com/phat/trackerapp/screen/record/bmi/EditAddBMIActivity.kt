package com.phat.trackerapp.screen.record.bmi

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
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
import com.phat.trackerapp.data.database.BMIDatabase
import com.phat.trackerapp.data.model.BMI
import com.phat.trackerapp.data.model.BMIType
import com.phat.trackerapp.database.SharePrefDB
import com.phat.trackerapp.extension.showDialogChooseDate
import com.phat.trackerapp.extension.showDialogChooseTime
import com.phat.trackerapp.extension.showNotice
import com.phat.trackerapp.screen.note.activity.EditAddNoteActivity
import com.phat.trackerapp.screen.record.adapter.NanoTagClass
import com.phat.trackerapp.screen.record.adapter.TagNoteAdapter
import com.phat.trackerapp.utils.Constants
import com.phat.trackerapp.utils.Utils
import kotlinx.android.synthetic.main.activity_edit_add_bmi.btnSave
import kotlinx.android.synthetic.main.activity_edit_add_bmi.edtHeight
import kotlinx.android.synthetic.main.activity_edit_add_bmi.edtWeight
import kotlinx.android.synthetic.main.activity_edit_add_bmi.layoutTypeInfo
import kotlinx.android.synthetic.main.activity_edit_add_bmi.sbState
import kotlinx.android.synthetic.main.activity_edit_add_bmi.tvDesBmi
import kotlinx.android.synthetic.main.activity_edit_add_bmi.txtTypeInfo
import kotlinx.android.synthetic.main.layout_date_time_option.btnDate
import kotlinx.android.synthetic.main.layout_date_time_option.btnTime
import kotlinx.android.synthetic.main.layout_date_time_option.layoutAddNote
import kotlinx.android.synthetic.main.layout_date_time_option.txtDate
import kotlinx.android.synthetic.main.layout_date_time_option.txtTime
import kotlinx.android.synthetic.main.layout_dialog_delete_tag.btnCancelDelete
import kotlinx.android.synthetic.main.layout_dialog_delete_tag.btnDeleteNote
import kotlinx.android.synthetic.main.layout_dialog_delete_tag.txtContent
import kotlinx.android.synthetic.main.layout_dialog_info_bmi.btnGotIt
import kotlinx.android.synthetic.main.layout_notes_dialog.btnCloseBpNote
import kotlinx.android.synthetic.main.layout_notes_dialog.btnEditAddNote
import kotlinx.android.synthetic.main.layout_notes_dialog.btnSaveNote
import kotlinx.android.synthetic.main.layout_notes_dialog.rvTags
import kotlinx.android.synthetic.main.layout_toolbar_edit_add_record.btnBack
import kotlinx.android.synthetic.main.layout_toolbar_edit_add_record.btnDelete
import kotlinx.android.synthetic.main.layout_toolbar_edit_add_record.txtTitle
import java.text.SimpleDateFormat
import java.util.Date

class EditAddBMIActivity : AppCompatActivity() {
    private lateinit var mInfoDialog: Dialog

    private lateinit var mBmiTypes: ArrayList<BMIType>

    private var mDialogNote: Dialog? = null

    private lateinit var mTagNoteAdapter: TagNoteAdapter

    private var mID: Int = 0

    private lateinit var mCurrentBMI: BMI

    private var mIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_add_bmi)
        initView()
        handleEvents()
    }

    private fun initView() {
        mID = intent.getIntExtra(Constants.ID,-1)
        mCurrentBMI = BMIDatabase.getInstance(this).BMIDao().findBMIById(mID)

        mInfoDialog = Utils.onCreateDialog(this, R.layout.layout_dialog_info_bmi)
        mBmiTypes = BMIType.getAllType()

        if(mID != -1 && mCurrentBMI != null){
            Log.d("222222",mCurrentBMI.id.toString() +"   "+mCurrentBMI.weight+"   ")
            //edit
            txtTitle.setText(R.string.txt_edit_record)
            btnDelete.visibility = View.VISIBLE

            edtWeight.setText(mCurrentBMI.weight.toInt().toString())
            edtHeight.setText(mCurrentBMI.height.toString())

            handleNumberic()
        }else{
            //add
            txtTitle.setText(R.string.txt_add_record)
        }

        initDateTime()

        mDialogNote = Utils.onCreateDialog(this,R.layout.layout_notes_dialog)
        mDialogNote?.window?.setGravity(Gravity.BOTTOM)

        val tags: ArrayList<String> = ArrayList()
        tags.addAll(
            SharePrefDB.getInstance(this).getAllNotes(Constants.KEY_BMI_NOTES).toList()
        )
        mTagNoteAdapter = TagNoteAdapter(this, tags)
        val nanoTagClass = NanoTagClass(this, mDialogNote?.rvTags!!, mTagNoteAdapter)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            mTagNoteAdapter.reloadTag(Constants.KEY_BMI_NOTES)
        }else{
            mDialogNote?.dismiss()
        }
    }

    private fun handleEvents() {
        layoutTypeInfo.setOnClickListener {
            mInfoDialog.show()
        }

        onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setResult(RESULT_CANCELED)
                finish()
            }
        })

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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
                if(mCurrentBMI != null){
                    mDialogDeleteNote.dismiss()
                    BMIDatabase.getInstance(this).BMIDao().delete(mCurrentBMI)
                    gotoBeforeScreen()
                }
            }
            mDialogDeleteNote.show()
        }

        mInfoDialog.btnGotIt.setOnClickListener {
            mInfoDialog.dismiss()
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

        btnSave.setOnClickListener {
            val id = if (mID == -1) 0 else mID
            if(edtHeight.text.toString().isNotEmpty() && edtWeight.text.toString().isNotEmpty()){
                if(isInvalidNumber(edtHeight.text.toString()) || isInvalidNumber(edtWeight.text.toString())){
                    showNotice(getString(R.string.txt_incorrect_number))
                }else{
                    val tag = mTagNoteAdapter.getAllTagSelected()
                    val bmi = BMI(
                        id = id,
                        weight = edtWeight.text.toString().toFloat(),
                        height = edtHeight.text.toString().toFloat(),
                        date = txtDate.text.toString(),
                        time = txtTime.text.toString(),
                        tag = tag
                    )
                    if(mID != -1 && mCurrentBMI != null){
                        BMIDatabase.getInstance(this@EditAddBMIActivity).BMIDao().update(bmi)
                    }else{
                        BMIDatabase.getInstance(this@EditAddBMIActivity).BMIDao().insert(bmi)
                    }

                    gotoBeforeScreen()
                }
            }else{
                showNotice(getString(R.string.txt_please_enter_content))
            }
        }

        mDialogNote?.apply {
            btnCloseBpNote?.setOnClickListener {
                dismiss()
            }

            btnEditAddNote?.setOnClickListener {

                val intent = Intent(this@EditAddBMIActivity, EditAddNoteActivity::class.java)
                intent.putExtra(Constants.KEY_NOTES, Constants.KEY_BMI_NOTES)
                showScreen(intent)
            }

            btnSaveNote?.setOnClickListener {

                dismiss()
            }
        }


        edtWeight.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode === KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                if (edtWeight.text.toString().isNullOrEmpty()) {
                    edtWeight.setText((65).toString())
                    edtWeight.setSelection(2)
                }else{
                    handleNumberic()
                }
            }
            false
        })

        edtHeight.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode === KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                if (edtHeight.text.toString().isNullOrEmpty()) {
                    edtHeight.setText((170).toString())
                    edtHeight.setSelection(3)
                } else {
                    handleNumberic()
                }
            }
            false
        })

//        edtWeight.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(
//                s: CharSequence?,
//                start: Int,
//                count: Int,
//                after: Int
//            ) {
//
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                if (s.toString().isNullOrEmpty()) {
//                    edtWeight.setText((255).toString())
//                    edtWeight.setSelection(3)
//                    showNotice(getString(R.string.txt_range_weight))
//                }
//            }
//
//        })
//
//        edtHeight.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(
//                s: CharSequence?,
//                start: Int,
//                count: Int,
//                after: Int
//            ) {
//
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                if (s.toString().isNullOrEmpty()) {
//                    edtHeight.setText((170).toString())
//                    edtHeight.setSelection(3)
//                    showNotice(getString(R.string.txt_range_height))
//                }
//            }
//        })
    }

    private fun showScreen(intent: Intent){
        mIntent = null
        mIntent = intent

        resultLauncher.launch(mIntent)
    }

    private fun gotoBeforeScreen() {
        setResult(RESULT_OK)
        finish()
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

    private fun setStateView(bmiValue: Float) {
        val position = BMIType.getPositionType(bmiValue)
        txtTypeInfo.setText(mBmiTypes[position].state)
        tvDesBmi.setText(mBmiTypes[position].valueRange)
        sbState.progress = position * 10 + 5
        layoutTypeInfo.backgroundTintList = ColorStateList.valueOf(mBmiTypes[position].color)
    }

    private fun isInvalidNumber(value: String): Boolean {
        if (value.toIntOrNull() == null) return true
        return (value.toInt() > 255 || value.toInt() < 1)
    }

    private fun handleNumberic() {
        val bmiValue = Utils.calculateBMI(
            edtWeight.text.toString().toFloat(),
            edtHeight.text.toString().toFloat()
        )
        setStateView(bmiValue)
    }

}