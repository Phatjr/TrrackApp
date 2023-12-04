package com.phat.trackerapp.utils.customview

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.phat.trackerapp.R
import kotlinx.android.synthetic.main.layout_item_unit_blood_sugar.view.ivArrow
import kotlinx.android.synthetic.main.layout_item_unit_blood_sugar.view.ivArrow2
import kotlinx.android.synthetic.main.layout_item_unit_blood_sugar.view.tvUnit1
import kotlinx.android.synthetic.main.layout_item_unit_blood_sugar.view.tvUnit2


class ItemUnitBloodSugar : FrameLayout {
    private val view: View

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        view = LayoutInflater
            .from(context)
            .inflate(R.layout.layout_item_unit_blood_sugar, this, true)

        context.obtainStyledAttributes(attrs, R.styleable.ItemTypeSugar).apply {
            recycle()
        }
    }

    var unitValue: Float = 18f
    set(value) {
        field = value
        if(value == 18f){
            unitText =  "mg/dL"
        }else{
            unitText =  "mmol/l"
        }
    }

    var unitText: String = "mg/dL"
        set(value) {
            field = value
        }

    var isUnitAbove: Boolean = true
        set(value) {
            field = value
            Log.d("2222222", isUnitAbove.toString())
            if (isUnitAbove) {
                view.ivArrow.visibility = View.VISIBLE
                view.tvUnit1.setTypeface(Typeface.DEFAULT_BOLD)
                view.tvUnit2.setTypeface(Typeface.DEFAULT)
                view.ivArrow2.visibility = View.INVISIBLE
                unitValue = 18f
            } else {
                view.ivArrow.visibility = View.INVISIBLE
                view.tvUnit2.setTypeface(Typeface.DEFAULT_BOLD)
                view.tvUnit1.setTypeface(Typeface.DEFAULT)
                view.ivArrow2.visibility = View.VISIBLE
                unitValue = 1f
            }
        }

}