package com.phat.trackerapp.screen.note.activity

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import com.phat.trackerapp.R
import com.phat.trackerapp.callback.OnItemListener
import com.phat.trackerapp.database.SharePrefDB
import com.phat.trackerapp.screen.note.adapter.ChipNoteAdapter
import com.phat.trackerapp.screen.note.adapter.NanoChipClass
import com.phat.trackerapp.screen.note.callback.ItemMoveCallback
import com.phat.trackerapp.utils.Constants
import com.phat.trackerapp.utils.Utils
import kotlinx.android.synthetic.main.activity_edit_add_note.btnAddNew
import kotlinx.android.synthetic.main.activity_edit_add_note.btnBack
import kotlinx.android.synthetic.main.activity_edit_add_note.btnSaveEditNote
import kotlinx.android.synthetic.main.activity_edit_add_note.rvNotes
import kotlinx.android.synthetic.main.layout_dialog_add_note.btnCancel
import kotlinx.android.synthetic.main.layout_dialog_add_note.btnSave
import kotlinx.android.synthetic.main.layout_dialog_add_note.edtContentNote
import kotlinx.android.synthetic.main.layout_dialog_delete_tag.btnCancelDelete
import kotlinx.android.synthetic.main.layout_dialog_delete_tag.btnDeleteNote

class EditAddNoteActivity : AppCompatActivity(), OnItemListener {

    private lateinit var mChips: ArrayList<String>

    private lateinit var mChipNoteAdapter: ChipNoteAdapter

    private lateinit var mNanoChipClass: NanoChipClass

    private var mDialogAddNote: Dialog? = null

    private var mDialogDeleteNote: Dialog? = null

    private var mPosDelete = 0

    private lateinit var mKeyNotes: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_add_note)
        initData()
        handleEvents()
    }

    // remove chip
    override fun onItemClick(position: Int) {
        mPosDelete = position
        mDialogDeleteNote?.show()
    }

    private fun initData() {
        mKeyNotes = intent.getStringExtra(Constants.KEY_NOTES).toString()
        mChips = ArrayList()
        mChips.addAll(SharePrefDB.getInstance(this).getAllNotes(mKeyNotes).toList())
        mChipNoteAdapter = ChipNoteAdapter(this, mChips, this)
        mNanoChipClass = NanoChipClass(this, rvNotes, mChipNoteAdapter)

        val callback: ItemTouchHelper.Callback = ItemMoveCallback(mChipNoteAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(rvNotes)

        mDialogAddNote = Utils.onCreateDialog(this,R.layout.layout_dialog_add_note)

        mDialogDeleteNote = Utils.onCreateDialog(this,R.layout.layout_dialog_delete_tag)
    }

    private fun handleEvents() {
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setResult(RESULT_CANCELED)
                finish()
            }
        })

        btnAddNew.setOnClickListener {
            mDialogAddNote?.show()
        }

        mDialogAddNote?.let {
            it.btnCancel.setOnClickListener {
                mDialogAddNote?.dismiss()
            }

            it.btnSave.setOnClickListener {
                val content = mDialogAddNote!!.edtContentNote.text?.trim().toString()
                if(!content.equals("")) {

                    mDialogAddNote?.dismiss()
                    mDialogAddNote?.edtContentNote?.setText("")
                    mChipNoteAdapter.addNote(content)
                }else{
                    Toast.makeText(this, "Please enter content!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        mDialogDeleteNote?.let {
            it.btnCancelDelete.setOnClickListener {
                mDialogDeleteNote?.dismiss()
            }

            it.btnDeleteNote.setOnClickListener {
                mDialogDeleteNote?.dismiss()
                mChipNoteAdapter.removeNote(mPosDelete)
            }
        }

        btnSaveEditNote.setOnClickListener {
            SharePrefDB.getInstance(this).setAllNotes(mKeyNotes, mChips.toSet())
            setResult(RESULT_OK)
            finish()
        }
    }

}