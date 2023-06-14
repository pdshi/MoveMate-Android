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
import com.stevennt.movemate.databinding.ActivityGoalBinding
import com.stevennt.movemate.databinding.ActivitySpareDayBinding
import com.stevennt.movemate.preference.UserPreferences
import com.stevennt.movemate.preference.UserPreferences.Companion.InputUserData

@Suppress("DEPRECATION")
class SpareDayActivity : AppCompatActivity(){

    private lateinit var binding: ActivitySpareDayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpareDayBinding.inflate(layoutInflater)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(binding.root)
        supportActionBar?.hide()

        binding.backSpare.setOnClickListener{
            onBackPressed()
        }

        binding.btnSpare.setOnClickListener{
            val goalWeight = binding.etGoalWeight.text.toString()
            val spareDays = binding.etSpareDay.text.toString()

            if (goalWeight.isNullOrEmpty() || spareDays.isNullOrEmpty()) {
                Toast.makeText(this@SpareDayActivity, R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
            } else if(spareDays < "3" || spareDays > "7") {
                Toast.makeText(this@SpareDayActivity, "Range Spare Days: 3-7", Toast.LENGTH_SHORT).show()
            } else {
                userDataArray[0].goalWeight = goalWeight
                userDataArray[0].frequency = spareDays

                val intent = Intent(this, StartWorkoutActivity::class.java)
                startActivity(intent)
            }
        }
    }
}