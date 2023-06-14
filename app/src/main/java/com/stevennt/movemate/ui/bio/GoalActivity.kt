package com.stevennt.movemate.ui.bio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.stevennt.movemate.R
import com.stevennt.movemate.data.model.userDataArray
import com.stevennt.movemate.databinding.ActivityAgeBinding
import com.stevennt.movemate.databinding.ActivityGoalBinding
import com.stevennt.movemate.preference.UserPreferences
import com.stevennt.movemate.preference.UserPreferences.Companion.InputUserData

@Suppress("DEPRECATION")
class GoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalBinding.inflate(layoutInflater)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(binding.root)
        supportActionBar?.hide()

        binding.backGoal.setOnClickListener{
            onBackPressed()
        }

        binding.loseWeightButton.setOnClickListener {
            userDataArray[0].goal = "lose_weight"

            val intent = Intent(this, SpareDayActivity::class.java)
            startActivity(intent)
        }

        binding.healthyButton.setOnClickListener {
            userDataArray[0].goal = "healthy"

            val intent = Intent(this, SpareDayActivity::class.java)
            startActivity(intent)
        }

        binding.gainMuscleButton.setOnClickListener {
            userDataArray[0].goal = "muscle_gain"

            val intent = Intent(this, SpareDayActivity::class.java)
            startActivity(intent)
        }
    }
}