package com.stevennt.movemate.ui.myplan

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.stevennt.movemate.R
import com.stevennt.movemate.data.Resource
import com.stevennt.movemate.data.model.Workouts
import com.stevennt.movemate.databinding.ActivityHomeBinding
import com.stevennt.movemate.databinding.ActivityMyPlanBinding
import com.stevennt.movemate.preference.UserPreferences
import com.stevennt.movemate.preference.UserPreferences.Companion.currentWorkoutIndex
import com.stevennt.movemate.preference.UserPreferences.Companion.userRepsList
import com.stevennt.movemate.preference.UserPreferences.Companion.workoutList
import com.stevennt.movemate.ui.ViewModelFactory
import com.stevennt.movemate.ui.home.ListWorkoutAdapter
import com.stevennt.movemate.ui.schedule.ScheduleViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.tensorflow.lite.examples.poseestimation.CameraActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class MyPlanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyPlanBinding
    private val list = ArrayList<Workouts>()
    private lateinit var dataStore: DataStore<Preferences>
    private val viewModel: MyPlanViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private var isDataFetched = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPlanBinding.inflate(layoutInflater)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(binding.root)
        supportActionBar?.hide()

        dataStore = PreferenceDataStoreFactory.create(
            produceFile = {
                File(application.filesDir, "user_prefs.preferences_pb")
            }
        )

        binding.backMyplan.setOnClickListener{
            workoutList.clear()
            onBackPressed()
        }

        binding.btnMyplan.setOnClickListener{
            currentWorkoutIndex = 1
            val intent = Intent(this@MyPlanActivity, CameraActivity::class.java)
            startActivity(intent)
        }

        if (!isDataFetched) {
            setupRecyclerView()
            isDataFetched = true
        }
    }

    @SuppressLint("Recycle")
    private fun setupRecyclerView() {
        val dataName = resources.getStringArray(R.array.workout_name_detail)
        val dataIcon = resources.obtainTypedArray(R.array.workout_icon)
        val dataInstruction = resources.getStringArray(R.array.workout_instruction)
        val dataFocusArea = resources.obtainTypedArray(R.array.workout_focus_area)

        val userPreferences = UserPreferences.getInstance(dataStore)
        val userSessionFlow = userPreferences.getUserSession()

        val currentDate = getCurrentDate()

        lifecycleScope.launch{
            userSessionFlow.collect{ userSession ->
                val sessionToken = userSession.token
                if (!sessionToken.isNullOrEmpty() && !isDataFetched) {
                    userRepsList.clear()
                    viewModel.getUserReps(sessionToken, currentDate).observe(this@MyPlanActivity) { result ->
                        when(result){
                            is Resource.Loading -> {

                            }

                            is Resource.Success -> {

                                Log.d("Date", currentDate)

                                val userRepsFlow = userPreferences.getUserReps()

                                val listWorkoutAdapter = ListWorkoutAdapter(emptyList())
                                binding.rvMyplan.adapter = listWorkoutAdapter
                                binding.rvMyplan.layoutManager = LinearLayoutManager(this@MyPlanActivity)
                                binding.rvMyplan.setHasFixedSize(true)



                                lifecycleScope.launch{
                                    userRepsFlow.collect{

                                        val sets = userRepsList.firstOrNull { it.date == currentDate + "T00:00:00.000Z" }?.sets
                                        val reps = userRepsList.firstOrNull { it.date == currentDate + "T00:00:00.000Z" }?.reps

                                        for (i in 0 until sets!!) {
                                            for (j in dataName.indices) {
                                                val workout = Workouts(
                                                    dataName[j],
                                                    dataIcon.getResourceId(j, -1),
                                                    reps.toString() + "x",
                                                    dataInstruction[j],
                                                    dataFocusArea.getResourceId(j, -1)
                                                )
                                                workoutList.add(workout)
                                            }
                                        }
                                        Log.d("workout", workoutList[3].name)
                                        listWorkoutAdapter.updateList(workoutList)
                                    }
                                }
                            }

                            is Resource.Error -> {
                                Toast.makeText(this@MyPlanActivity, result.message, Toast.LENGTH_SHORT).show()
                                Log.d(this@MyPlanActivity.toString(), "msg: ${result.message}")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate).toString()
    }
}