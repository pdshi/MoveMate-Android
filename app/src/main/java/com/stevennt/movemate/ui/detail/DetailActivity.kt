package com.stevennt.movemate.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.stevennt.movemate.R
import com.stevennt.movemate.data.model.Workouts
import com.stevennt.movemate.databinding.ActivityDetailBinding
import com.stevennt.movemate.databinding.ActivityMainBinding

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)

        // Hide the status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(binding.root)

        supportActionBar?.hide()

        binding.backDetail.setOnClickListener{
            onBackPressed()
        }

        val dataWorkouts = intent.getParcelableExtra<Workouts>("key_workouts")!!

        binding.tvDetail.text = dataWorkouts.name
        binding.ivDetail.setImageResource(dataWorkouts.icon)
        binding.tvInstruction.text = dataWorkouts.instruction
        dataWorkouts.focusArea?.let { binding.ivMuscleDetail.setImageResource(it) }

    }
}