package com.phat.trackerapp.utils.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.phat.trackerapp.R
import kotlinx.android.synthetic.main.layout_item_unit.view.tvDes
import kotlinx.android.synthetic.main.layout_item_unit.view.tvTitleUnit
import kotlinx.android.synthetic.main.layout_item_unit.view.txtValueUnit


class ItemUnit : FrameLayout {
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
            .inflate(R.layout.layout_item_unit, this, true)

        context.obtainStyledAttributes(attrs, R.styleable.ItemUnit).apply {
            view.tvTitleUnit.text = this.getString(R.styleable.ItemUnit_txtTitle).orEmpty()
            view.tvDes.text = this.getString(R.styleable.ItemUnit_txtDes).orEmpty()
            view.txtValueUnit.setTextColor(this.getColor(R.styleable.ItemUnit_numberColor,ContextCompat.getColor(context,R.color.black)))
            recycle()
        }
    }

    var value: String = "0"
    set(value) {
        field = value
        view.txtValueUnit.text = value
    }

}