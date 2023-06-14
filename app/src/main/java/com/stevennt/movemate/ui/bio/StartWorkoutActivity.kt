package com.stevennt.movemate.ui.bio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.stevennt.movemate.R
import com.stevennt.movemate.data.model.userDataArray
import com.stevennt.movemate.databinding.ActivitySpareDayBinding
import com.stevennt.movemate.databinding.ActivityStartWorkoutBinding
import com.stevennt.movemate.preference.UserPreferences
import com.stevennt.movemate.preference.UserPreferences.Companion.InputUserData

@Suppress("DEPRECATION")
class StartWorkoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartWorkoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartWorkoutBinding.inflate(layoutInflater)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(binding.root)
        supportActionBar?.hide()

        binding.backStartWorkout.setOnClickListener{
            onBackPressed()
        }

        binding.btnToday.setOnClickListener {
            userDataArray[0].dayStart = "today"

            val intent = Intent(this, AvailableTimeActivity::class.java)
            startActivity(intent)
        }

        binding.btnTomorrow.setOnClickListener {
            userDataArray[0].dayStart = "tomorrow"

            val intent = Intent(this, AvailableTimeActivity::class.java)
            startActivity(intent)
        }
    }
}