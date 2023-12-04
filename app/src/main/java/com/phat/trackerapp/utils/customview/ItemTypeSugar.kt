package com.phat.trackerapp.utils.customview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.phat.trackerapp.R
import kotlinx.android.synthetic.main.layout_item_value_sugar.view.ivArrow
import kotlinx.android.synthetic.main.layout_item_value_sugar.view.lineState
import kotlinx.android.synthetic.main.layout_item_value_sugar.view.tvState
import kotlinx.android.synthetic.main.layout_item_value_sugar.view.tvValueRange


class ItemTypeSugar : FrameLayout {
    private val view: View

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        view = LayoutInflater
            .from(context)
            .inflate(R.layout.layout_item_value_sugar, this, true)

        context.obtainStyledAttributes(attrs, R.styleable.ItemTypeSugar).apply {
            showText(this)
            showColor(this)
            recycle()
        }
    }

    var visible: Boolean = true
    set(value) {
        setBtnSelected(value)
        field = value
    }

    var valueRange: String = ""
        set(value) {
            field = value
            view.tvValueRange.setText(valueRange)
        }
    var color: Int  = 0
        set(value) {
            field = value
        }

    var state: String  = ""
        set(value) {
            field = value
        }

    private fun setBtnSelected(isSelected: Boolean){
        if(isSelected){
            view.ivArrow.visibility = View.VISIBLE
            view.tvState.setTypeface(Typeface.DEFAULT_BOLD)
            view.tvValueRange.setTypeface(Typeface.DEFAULT_BOLD)
        }else{
            view.ivArrow.visibility = View.INVISIBLE
            view.tvState.setTypeface(Typeface.DEFAULT)
            view.tvValueRange.setTypeface(Typeface.DEFAULT)
        }
    }

    private fun showColor(typedArray: TypedArray) {
        setBtnSelected(typedArray.getBoolean(R.styleable.ItemTypeSugar_visible, false))
        color = typedArray.getColor(R.styleable.ItemTypeSugar_defaultColor, 0)
        view.ivArrow.setColorFilter(color)
        view.lineState.background.setColorFilter(typedArray.getColor(R.styleable.ItemTypeSugar_defaultColor, 0),PorterDuff.Mode.SRC_IN)
    }

    private fun showText(attributes: TypedArray) {
        state = attributes.getString(R.styleable.ItemTypeSugar_txtState).orEmpty()
        view.tvState.text = state
        view.tvValueRange.text = attributes.getString(R.styleable.ItemTypeSugar_txtRange).orEmpty()
    }

}