package com.stevennt.movemate.ui.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.stevennt.movemate.R

class CustomButton: AppCompatButton {

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        background = ContextCompat.getDrawable(context, R.drawable.button_round) as Drawable
        textAlignment = View.TEXT_ALIGNMENT_CENTER
        textSize = 13F
        setHintTextColor(ContextCompat.getColor(context, R.color.offWhite))
    }

    private fun initView() {
        // Apply custom styling or behavior here
        // For example, set background color and text case
        setBackgroundColor(Color.parseColor("#FF5834"))
        isAllCaps = false
    }
}