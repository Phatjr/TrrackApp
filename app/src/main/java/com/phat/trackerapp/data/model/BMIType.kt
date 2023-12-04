package com.phat.trackerapp.data.model

import android.util.Log
import com.phat.trackerapp.R

class BMIType(var state: Int, var color: Int, var valueRange: Int) {

    companion object {
        fun getPositionType(bmiValue: Float): Int {
            Log.d("5555555", bmiValue.toString())
            when (bmiValue) {
                in 0.0..18.49 -> {
                    return 0
                }
                in 18.5..24.5 -> {
                    return 1
                }
                in 25.0..29.9 -> {
                    return 2
                }
                in 30.0..34.99 -> {
                    return 3
                }
                in 35.0..39.99 -> {
                    return 4
                }
                else -> {
                    return 5
                }
            }
        }

        fun getAllType(): ArrayList<BMIType>{
            val mBmiTypes = ArrayList<BMIType>()
            mBmiTypes.add(
                BMIType(
                    R.string.txt_bmi_type_1,
                    R.color.color_bmi_type_1,
                    R.string.txt_des_bmi_type_1
                )
            )
            mBmiTypes.add(
                BMIType(
                    R.string.txt_bmi_type_2,
                    R.color.color_bmi_type_2,
                    R.string.txt_des_bmi_type_2
                )
            )
            mBmiTypes.add(
                BMIType(
                    R.string.txt_bmi_type_3,
                    R.color.color_bmi_type_3,
                    R.string.txt_des_bmi_type_3
                )
            )
            mBmiTypes.add(
                BMIType(
                    R.string.txt_bmi_type_4,
                    R.color.color_bmi_type_4,
                    R.string.txt_des_bmi_type_4
                )
            )
            mBmiTypes.add(
                BMIType(
                    R.string.txt_bmi_type_5,
                    R.color.color_bmi_type_5,
                    R.string.txt_des_bmi_type_5
                )
            )
            mBmiTypes.add(
                BMIType(
                    R.string.txt_bmi_type_6,
                    R.color.color_bmi_type_6,
                    R.string.txt_des_bmi_type_6
                )
            )
           return mBmiTypes
        }
    }
}