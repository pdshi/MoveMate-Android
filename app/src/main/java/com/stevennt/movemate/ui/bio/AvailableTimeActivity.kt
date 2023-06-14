package com.stevennt.movemate.ui.bio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.stevennt.movemate.R
import com.stevennt.movemate.data.model.userDataArray
import com.stevennt.movemate.databinding.ActivityAvailableTimeBinding
import com.stevennt.movemate.databinding.ActivityStartWorkoutBinding
import com.stevennt.movemate.preference.UserPreferences
import com.stevennt.movemate.preference.UserPreferences.Companion.InputUserData
import com.stevennt.movemate.ui.getplan.GetPlanActivity

@Suppress("DEPRECATION")
class AvailableTimeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAvailableTimeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAvailableTimeBinding.inflate(layoutInflater)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(binding.root)
        supportActionBar?.hide()

        binding.backAvailableTime.setOnClickListener{
            onBackPressed()
        }

        binding.btnMorning.setOnClickListener {
            userDataArray[0].woTime = "morning"

            val intent = Intent(this, GetPlanActivity::class.java)
            startActivity(intent)
        }

        binding.btnAfternoon.setOnClickListener {
            userDataArray[0].woTime = "afternoon"

            val intent = Intent(this, GetPlanActivity::class.java)
            startActivity(intent)
        }

        binding.btnNight.setOnClickListener {
            userDataArray[0].woTime = "night"

            val intent = Intent(this, GetPlanActivity::class.java)
            startActivity(intent)
        }
    }
}