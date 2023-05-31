package com.stevennt.movemate.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.stevennt.movemate.R
import com.stevennt.movemate.databinding.ActivityDailyBinding
import com.stevennt.movemate.databinding.ActivityProfileBinding
import com.stevennt.movemate.ui.daily.DailyActivity
import com.stevennt.movemate.ui.home.HomeActivity

@Suppress("DEPRECATION")
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(binding.root)
        supportActionBar?.hide()

        binding.ivHomeAtprofile.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        binding.ivDailyAtprofile.setOnClickListener{
            val intent = Intent(this, DailyActivity::class.java)
            startActivity(intent)
        }
    }
}