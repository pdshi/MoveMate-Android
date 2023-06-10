package com.stevennt.movemate.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.stevennt.movemate.Injection
import com.stevennt.movemate.R
import com.stevennt.movemate.data.model.Workouts
import com.stevennt.movemate.databinding.ActivityHomeBinding
import com.stevennt.movemate.ui.auth.LoginActivity
import com.stevennt.movemate.ui.schedule.ScheduleActivity
import com.stevennt.movemate.ui.myplan.MyPlanActivity
import com.stevennt.movemate.ui.profile.ProfileActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
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

        binding.ivDailyAthome.setOnClickListener{
            val intent = Intent(this, ScheduleActivity::class.java)
            setResult(Activity.RESULT_OK)
            startActivity(intent)
        }

        binding.ivProfileAthome.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.ivMyplan.setOnClickListener{
            val intent = Intent(this, MyPlanActivity::class.java)
            startActivity(intent)
        }

        setupRecyclerView()
        getUserSession()
    }

    @SuppressLint("Recycle")
    private fun setupRecyclerView() {
        val dataName = resources.getStringArray(R.array.workout_name_detail)
        val dataIcon = resources.obtainTypedArray(R.array.workout_icon)
        val dataReps = resources.getStringArray(R.array.workout_reps)
        val dataInstruction = resources.getStringArray(R.array.workout_instruction)
        val dataFocusArea = resources.obtainTypedArray(R.array.workout_focus_area)

        for (i in dataName.indices) {
            val workout = Workouts(
                dataName[i],
                dataIcon.getResourceId(i, -1),
                dataReps[i],
                dataInstruction[i],
                dataFocusArea.getResourceId(i, -1)
            )
            list.add(workout)
        }

        binding.rvListWorkout.layoutManager = LinearLayoutManager(this)
        val listWorkoutAdapter = ListWorkoutAdapter(list)
        binding.rvListWorkout.adapter = listWorkoutAdapter
        binding.rvListWorkout.setHasFixedSize(true)
    }

    private fun getUserSession() {
        val userPreferences = Injection.provideUserPreferences(this@HomeActivity)
        lifecycleScope.launch {
            val userSession = userPreferences.getUserSession().first()
            if (userSession.token?.isEmpty() == true) {
                val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
        }
    }
}