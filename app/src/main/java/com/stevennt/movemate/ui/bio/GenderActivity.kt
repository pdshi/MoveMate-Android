package com.stevennt.movemate.ui.bio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.stevennt.movemate.R
import com.stevennt.movemate.databinding.ActivityGenderBinding
import com.stevennt.movemate.databinding.ActivityLoginBinding
import com.stevennt.movemate.ui.auth.RegisterActivity
import com.stevennt.movemate.ui.home.HomeActivity

@Suppress("DEPRECATION")
class GenderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGenderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenderBinding.inflate(layoutInflater)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(binding.root)
        supportActionBar?.hide()

        binding.ivMale.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}