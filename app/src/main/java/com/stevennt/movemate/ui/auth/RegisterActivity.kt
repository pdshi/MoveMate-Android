package com.stevennt.movemate.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.stevennt.movemate.R
import com.stevennt.movemate.data.Resource
import com.stevennt.movemate.databinding.ActivityRegisterBinding
import com.stevennt.movemate.ui.ViewModelFactory
import com.stevennt.movemate.ui.utils.CustomEditText

@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var activity: RegisterActivity
    private val viewModel: AuthViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(binding.root)

        supportActionBar?.hide()
        activity = this

        binding.backRegis.setOnClickListener{
            onBackPressed()
        }

        setUp()
    }

    private fun CustomEditText.listener() {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                with(binding) {
                    btnRegis.isEnabled = etEmailregis.isValid && etPasswordregis.isValid
                }
            }

        })
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.btn_regis -> {
                val email = binding.etEmailregis.text?.toString()
                val password = binding.etPasswordregis.text?.toString()
                val confirmPassword = binding.etConfirmpassword.text?.toString()

                if (email.isNullOrEmpty() || password.isNullOrEmpty() || confirmPassword.isNullOrEmpty()) {
                    Toast.makeText(this@RegisterActivity, R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
                    return
                }

                if(password == confirmPassword){
                    viewModel.register(email, password, key = "supersecretapikey").observe(this@RegisterActivity) { result ->
                        if(result != null) {
                            when(result) {
                                is Resource.Loading -> {
                                    showLoading(true)
                                }
                                is Resource.Success -> {
                                    showLoading(false)
                                    Intent(this@RegisterActivity, LoginActivity::class.java).apply {
                                        startActivity(this)
                                        finish()
                                    }
                                }
                                is Resource.Error -> {
                                    showLoading(false)
                                    Toast.makeText(this@RegisterActivity, "User Already Exists!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                } else{
                    Toast.makeText(this, getString(R.string.password_not_match), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setUp(){
        with(binding){
            btnRegis.setOnClickListener(activity)
            etEmailregis.listener()
            etPasswordregis.listener()
            etConfirmpassword.listener()
        }
    }

    private fun showLoading(loadingState: Boolean) {
        with(binding.btnRegis) {
            isEnabled = !loadingState
            text = context.getString(if (loadingState) R.string.loading else R.string.register_inactivity)
        }
    }
}