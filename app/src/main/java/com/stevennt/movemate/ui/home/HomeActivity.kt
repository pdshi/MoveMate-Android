package com.stevennt.movemate.ui.home

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stevennt.movemate.R
import com.stevennt.movemate.data.model.Workouts
import com.stevennt.movemate.databinding.ActivityDetailBinding
import com.stevennt.movemate.databinding.ActivityHomeBinding

@Suppress("DEPRECATION")
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var rvWorkout: RecyclerView
    private val list = ArrayList<Workouts>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(binding.root)

        supportActionBar?.hide()

        rvWorkout = binding.rvListWorkout
        list.addAll(getListWorkout())
        showRecyclerList()
    }

    @SuppressLint("Recycle")
    private fun getListWorkout(): ArrayList<Workouts> {
        val dataName = resources.getStringArray(R.array.workout_name_detail)
        val dataIcon = resources.obtainTypedArray(R.array.workout_icon)
        val dataInstruction = resources.getStringArray(R.array.workout_instruction)
        val dataFocusArea = resources.obtainTypedArray(R.array.workout_focus_area)
        val listWorkout = ArrayList<Workouts>()
        for (i in dataName.indices) {
            val workout = Workouts(dataName[i], dataIcon.getResourceId(i, -1), dataInstruction[i], dataFocusArea.getResourceId(i, -1))
            listWorkout.add(workout)
        }
        return listWorkout
    }

    private fun showRecyclerList() {
        rvWorkout.layoutManager = LinearLayoutManager(this)
        val listWorkoutAdapter = ListWorkoutAdapter(list)
        rvWorkout.adapter = listWorkoutAdapter
        rvWorkout.setHasFixedSize(true)
    }
}