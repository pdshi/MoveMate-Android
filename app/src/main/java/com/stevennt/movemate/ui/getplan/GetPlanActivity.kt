package com.stevennt.movemate.ui.getplan

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.lifecycleScope
import com.stevennt.movemate.data.Resource
import com.stevennt.movemate.data.model.userDataArray
import com.stevennt.movemate.databinding.ActivityGetPlanBinding
import com.stevennt.movemate.preference.UserPreferences
import com.stevennt.movemate.preference.UserPreferences.Companion.email
import com.stevennt.movemate.preference.UserPreferences.Companion.password
import com.stevennt.movemate.ui.ViewModelFactory
import com.stevennt.movemate.ui.auth.AuthViewModel
import com.stevennt.movemate.ui.bio.BioViewModel
import com.stevennt.movemate.ui.welcome.WelcomeActivity
import kotlinx.coroutines.launch
import java.io.File
import com.stevennt.movemate.ui.auth.LoginActivity
import java.time.LocalDate

@Suppress("DEPRECATION")
class GetPlanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGetPlanBinding
    private lateinit var dataStore: DataStore<Preferences>
    private val viewModel: BioViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val viewModelLogin: AuthViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private var isDataFetched = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetPlanBinding.inflate(layoutInflater)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
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

        val userPreferences = UserPreferences.getInstance(dataStore)
        val userSessionFlow = userPreferences.getUserSession()

        lifecycleScope.launch{
            userSessionFlow.collect { userSession ->
                val sessionToken = userSession.token
                if (!sessionToken.isNullOrEmpty() && !isDataFetched) {
                    viewModel.inputUserData(
                        sessionToken,
                        userDataArray[0].displayName,
                        userDataArray[0].photoUrl,
                        userDataArray[0].gender,
                        userDataArray[0].age,
                        userDataArray[0].height,
                        userDataArray[0].weight,
                        userDataArray[0].goal,
                        userDataArray[0].goalWeight,
                        userDataArray[0].frequency,
                        userDataArray[0].dayStart,
                        userDataArray[0].woTime,
                    ).observe(this@GetPlanActivity){ result ->
                        when(result){
                            is Resource.Loading -> {}

                            is Resource.Success -> {
                                isDataFetched = true
                                viewModelLogin.getUserData(sessionToken).observe(this@GetPlanActivity) { result ->
                                    when(result){
                                        is Resource.Loading -> {}
                                        is Resource.Success -> {
                                            isDataFetched = true

                                            val today = LocalDate.now()
                                            val incrementInterval = 5
                                            val totalDays = 14
                                            var sets = 2
                                            var end = "16.00"

                                            for(day in 0 until totalDays){
                                                if (day % incrementInterval == 0 && day != 0) {
                                                    sets++
                                                    val currentEnd = end.toFloat()
                                                    end = (currentEnd + 0.03).toString()
                                                }

                                                val currentDate = today.plusDays(day.toLong())
                                                val formattedDate = currentDate.toString()

                                                viewModel.inputUserReps(
                                                    sessionToken,
                                                    "pushup",
                                                    "12",
                                                    sets.toString(),
                                                    formattedDate,
                                                    "16.00",
                                                    end
                                                ).observe(this@GetPlanActivity){ result ->
                                                    when(result){
                                                        is Resource.Loading -> {}

                                                        is Resource.Success -> {
                                                            viewModel.inputUserReps(
                                                                sessionToken,
                                                                "situp",
                                                                "12",
                                                                sets.toString(),
                                                                formattedDate,
                                                                "16.00",
                                                                end
                                                            ).observe(this@GetPlanActivity){result ->
                                                                when(result) {
                                                                    is Resource.Loading -> {}

                                                                    is Resource.Success -> {}

                                                                    is Resource.Error -> {}
                                                                }
                                                            }
                                                        }

                                                        is Resource.Error -> {}
                                                    }
                                                }
                                            }

                                            val intent = Intent(this@GetPlanActivity, WelcomeActivity::class.java)
                                            startActivity(intent)
                                        }
                                        is Resource.Error -> {
                                            Toast.makeText(this@GetPlanActivity, result.message, Toast.LENGTH_SHORT).show()
                                            Log.d(this@GetPlanActivity.toString(), "msg: ${result.message}")
                                        }
                                    }
                                }
                            }

                            is Resource.Error -> {
                                Toast.makeText(this@GetPlanActivity, result.message, Toast.LENGTH_SHORT).show()
                                Log.d(this@GetPlanActivity.toString(), "msg: ${result.message}")
                            }
                        }
                    }
                }
            }
        }
    }
}