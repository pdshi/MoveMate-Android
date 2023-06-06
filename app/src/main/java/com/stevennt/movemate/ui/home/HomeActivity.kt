package com.stevennt.movemate.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stevennt.movemate.Injection
import com.stevennt.movemate.R
import com.stevennt.movemate.data.model.Workouts
import com.stevennt.movemate.databinding.ActivityDetailBinding
import com.stevennt.movemate.databinding.ActivityHomeBinding
import com.stevennt.movemate.preference.UserPreferences
import com.stevennt.movemate.ui.auth.LoginActivity
import com.stevennt.movemate.ui.daily.DailyActivity
import com.stevennt.movemate.ui.detail.DetailActivity
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
            val intent = Intent(this, DailyActivity::class.java)
            startActivity(intent)
        }

        binding.ivProfileAthome.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        setupRecyclerView()
        getUserSession()
    }

    @SuppressLint("Recycle")
    private fun setupRecyclerView() {
        val dataName = resources.getStringArray(R.array.workout_name_detail)
        val dataIcon = resources.obtainTypedArray(R.array.workout_icon)
        val dataInstruction = resources.getStringArray(R.array.workout_instruction)
        val dataFocusArea = resources.obtainTypedArray(R.array.workout_focus_area)

        for (i in dataName.indices) {
            val workout = Workouts(dataName[i], dataIcon.getResourceId(i, -1), dataInstruction[i], dataFocusArea.getResourceId(i, -1))
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