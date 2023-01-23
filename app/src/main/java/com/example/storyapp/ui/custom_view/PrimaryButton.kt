package com.example.storyapp.ui.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class PrimaryButton: AppCompatButton {
    private lateinit var enabledBackground: Drawable
    private lateinit var disabledBackground: Drawable
    private var txtColor: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        background = if (isEnabled) enabledBackground else disabledBackground

        setTextColor(txtColor)
        textSize = 16f
        gravity = Gravity.CENTER
        text = if (isEnabled) "Submit" else "Fill The Blank"
    }

    private fun init() {
        txtColor = ContextCompat.getColor(context, R.color.text_primary)
        enabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_primary_button) as Drawable
        disabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_primary_button_disable) as Drawable
    }
}