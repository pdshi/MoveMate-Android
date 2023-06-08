package com.stevennt.movemate.ui.myplan

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.stevennt.movemate.R
import com.stevennt.movemate.data.model.Workouts
import com.stevennt.movemate.databinding.ActivityHomeBinding
import com.stevennt.movemate.databinding.ActivityMyPlanBinding
import com.stevennt.movemate.ui.home.ListWorkoutAdapter

@Suppress("DEPRECATION")
class MyPlanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyPlanBinding
    private val list = ArrayList<Workouts>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPlanBinding.inflate(layoutInflater)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(binding.root)
        supportActionBar?.hide()

        binding.backMyplan.setOnClickListener{
            onBackPressed()
        }

        setupRecyclerView()
    }

    @SuppressLint("Recycle")
    private fun setupRecyclerView() {
        val dataName = resources.getStringArray(R.array.workout_name_detail)
        val dataIcon = resources.obtainTypedArray(R.array.workout_icon)
        val dataInstruction = resources.getStringArray(R.array.workout_instruction)
        val dataFocusArea = resources.obtainTypedArray(R.array.workout_focus_area)

        val n = 2

        for (iteration in 0 until n) {
            for (i in dataName.indices) {
                val workout = Workouts(
                    dataName[i],
                    dataIcon.getResourceId(i, -1),
                    dataInstruction[i],
                    dataFocusArea.getResourceId(i, -1)
                )
                list.add(workout)
            }
        }

        binding.rvMyplan.layoutManager = LinearLayoutManager(this)
        val listWorkoutAdapter = ListWorkoutAdapter(list)
        binding.rvMyplan.adapter = listWorkoutAdapter
        binding.rvMyplan.setHasFixedSize(true)
    }
}