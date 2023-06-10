package com.stevennt.movemate.ui.myplan

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
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
import com.stevennt.movemate.preference.UserPreferences.Companion.userRepsList
import com.stevennt.movemate.ui.ViewModelFactory
import com.stevennt.movemate.ui.home.ListWorkoutAdapter
import com.stevennt.movemate.ui.schedule.ScheduleViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.tensorflow.lite.examples.poseestimation.CameraActivity
import java.io.File

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
            onBackPressed()
        }

        binding.btnMyplan.setOnClickListener{
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

        lifecycleScope.launch{
            userSessionFlow.collect{ userSession ->
                val sessionToken = userSession.token
                if (!sessionToken.isNullOrEmpty() && !isDataFetched) {
                    viewModel.getUserReps(sessionToken, "2023-06-09").observe(this@MyPlanActivity) { result ->
                        when(result){
                            is Resource.Loading -> {

                            }

                            is Resource.Success -> {

                                val userRepsFlow = userPreferences.getUserReps()

                                val listWorkoutAdapter = ListWorkoutAdapter(emptyList())
                                binding.rvMyplan.adapter = listWorkoutAdapter
                                binding.rvMyplan.layoutManager = LinearLayoutManager(this@MyPlanActivity)
                                binding.rvMyplan.setHasFixedSize(true)

                                val list = mutableListOf<Workouts>()
                                lifecycleScope.launch{
                                    userRepsFlow.collect{

                                        val set = userRepsList[1].sets
                                        val rep = userRepsList[1].reps

                                        for (i in 0 until set!!) {
                                            for (j in dataName.indices) {
                                                val workout = Workouts(
                                                    dataName[j],
                                                    dataIcon.getResourceId(j, -1),
                                                    rep.toString() + "x",
                                                    dataInstruction[j],
                                                    dataFocusArea.getResourceId(j, -1)
                                                )
                                                list.add(workout)
                                            }
                                        }
                                        listWorkoutAdapter.updateList(list)
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
}