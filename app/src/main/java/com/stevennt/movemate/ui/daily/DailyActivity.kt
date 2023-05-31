package com.stevennt.movemate.ui.daily

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.stevennt.movemate.R
import com.stevennt.movemate.databinding.ActivityDailyBinding
import com.stevennt.movemate.databinding.ActivityHomeBinding
import com.stevennt.movemate.ui.home.HomeActivity
import com.stevennt.movemate.ui.profile.ProfileActivity

@Suppress("DEPRECATION")
class DailyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDailyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailyBinding.inflate(layoutInflater)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(binding.root)
        supportActionBar?.hide()

        binding.ivHomeAtdaily.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        binding.ivProfileAtdaily.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}