package com.stevennt.movemate.ui.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.view.marginRight
import com.stevennt.movemate.R

class CustomEditText: androidx.appcompat.widget.AppCompatEditText {
    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }
    var txtColor: Int = 0
    var isValid: Boolean = false

    var password: String = ""
    var confirmPassword: String = ""

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        background = ContextCompat.getDrawable(context, R.drawable.border_edittext) as Drawable
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        textSize = 12F
        setHintTextColor(ContextCompat.getColor(context, R.color.gray))
    }

    private fun initView() {
        txtColor = ContextCompat.getColor(context, R.color.offWhite)
        this.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {

                password = p0?.toString() ?: ""

                if (text != null && text?.isNotEmpty() == true) {
                    when(inputType) {
                        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS or InputType.TYPE_CLASS_TEXT-> {
                            if (!Patterns.EMAIL_ADDRESS.matcher(text.toString()).matches() && text!!.isNotEmpty()) {
                                error = "Email not valid"
                                isValid = false
                            } else {
                                isValid = true
                            }
                        }
                        InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT-> {
                            if (text!!.toString().length < 8 && text?.isNotEmpty()!!) {
                                error = "Password must be at least 8 characters"
                                isValid = false
                            } else {

                                // Validate confirm password
                                val confirmPasswordEditText: EditText = findViewById(R.id.editTextTextConfirmPassword_register)
                                val confirmPassword = confirmPasswordEditText.text?.toString() ?: ""

                                if (text!!.toString() != confirmPassword) {
                                    confirmPasswordEditText.error = "Passwords do not match"
                                    isValid = false
                                } else {
                                    isValid = true
                                }
                            }
                        }
                    }
                }else{
                    error = "Error"
                    isValid = false
                }
            }
        })
    }
}