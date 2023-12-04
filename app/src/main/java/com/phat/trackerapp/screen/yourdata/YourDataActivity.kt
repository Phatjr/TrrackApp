package com.phat.trackerapp.screen.yourdata

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.phat.trackerapp.R
import com.phat.trackerapp.database.SharePrefDB
import com.phat.trackerapp.extension.isDecimal
import com.phat.trackerapp.extension.showNotice
import com.phat.trackerapp.extension.textString
import com.phat.trackerapp.screen.home.HomeActivity
import com.phat.trackerapp.screen.yourdata.model.User
import com.phat.trackerapp.utils.Utils
import kotlinx.android.synthetic.main.activity_your_data.btnSave
import kotlinx.android.synthetic.main.activity_your_data.edtAge
import kotlinx.android.synthetic.main.activity_your_data.edtHeight
import kotlinx.android.synthetic.main.activity_your_data.edtWeight
import kotlinx.android.synthetic.main.activity_your_data.rbFemale
import kotlinx.android.synthetic.main.activity_your_data.rbMale
import kotlinx.android.synthetic.main.activity_your_data.rbOther
import kotlinx.android.synthetic.main.activity_your_data.spHeight
import kotlinx.android.synthetic.main.activity_your_data.spWeight

class YourDataActivity : AppCompatActivity() {
    private lateinit var mSharePrefDB: SharePrefDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setLanguageForApp(this)
        setContentView(R.layout.activity_your_data)
        initView()
        handleEvent()
    }

    private fun initView() {
        mSharePrefDB = SharePrefDB(this)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayOf("cm", "ft"))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spHeight.setAdapter(adapter)

        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayOf("kg", "lb"))
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spWeight.setAdapter(adapter2)
        val user = mSharePrefDB.getUser()
        if (user != null) {
            when (user.gender) {
                0 -> rbFemale.isChecked = true
                1 -> rbMale.isChecked = true
                2 -> rbOther.isChecked = true
            }

            edtAge.setText(user.age.toString())
            edtHeight.setText(user.height.toString())
            edtWeight.setText(user.weight.toString())
            spHeight.setSelection(user.unitHeight)
            spWeight.setSelection(user.unitWeight)
        }
    }


    private fun handleEvent() {
        edtAge.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode === KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_NEXT) {
                edtHeight.setSelection(edtHeight.textString().length)
            }
            false
        })

        edtHeight.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode === KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_NEXT) {
                edtWeight.setSelection(edtWeight.textString().length)
            }
            false
        })

        btnSave.setOnClickListener {
            if (edtAge.textString().isNullOrBlank() || edtHeight.textString()
                    .isNullOrBlank() || edtWeight.textString().isNullOrBlank()
            ) {
                showNotice(getString(R.string.txt_please_enter_content))
            } else if (!edtAge.isDecimal() || !edtWeight.isDecimal() || !edtHeight.isDecimal()) {
                showNotice(getString(R.string.txt_please_enter_valid_content))
            } else {
                val gender = if (rbFemale.isChecked) {
                    0
                } else if (rbMale.isChecked) {
                    1
                } else {
                    2
                }
                val age = edtAge.textString().toInt()
                val height = edtHeight.textString().toFloat()
                val weight = edtWeight.textString().toFloat()
                val unitHeight = spHeight.selectedItemPosition
                val unitWeight = spWeight.selectedItemPosition

                if (weight > 300 || height > 220 || age > 110) {
                    showNotice(getString(R.string.txt_invalid_height))
                } else {
                    if(mSharePrefDB.getUser() != null){
                        mSharePrefDB.setUser(User(gender, age, height, weight, unitHeight, unitWeight))
                        finish()
                    }else{
                        val intent = Intent(this, HomeActivity::class.java)
                        mSharePrefDB.setUser(User(gender, age, height, weight, unitHeight, unitWeight))
                        startActivity(intent)
                    }

                }
            }
        }
    }

}