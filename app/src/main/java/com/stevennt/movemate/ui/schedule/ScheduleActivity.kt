package com.stevennt.movemate.ui.schedule

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.lifecycleScope
import com.stevennt.movemate.R
import com.stevennt.movemate.data.Resource
import com.stevennt.movemate.databinding.ActivityScheduleBinding
import com.stevennt.movemate.preference.UserPreferences
import com.stevennt.movemate.preference.UserPreferences.Companion.userHistories
import com.stevennt.movemate.ui.ViewModelFactory
import com.stevennt.movemate.ui.home.HomeActivity
import com.stevennt.movemate.ui.profile.ProfileActivity
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION", "KotlinConstantConditions")
class ScheduleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScheduleBinding
    private val viewModel: ScheduleViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var activityLauncher: ActivityResultLauncher<Intent>


    private var selectedStartDate: String? = null
    private var selectedEndDate: String? = null
    private var isDataFetched = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = com.stevennt.movemate.databinding.ActivityScheduleBinding.inflate(layoutInflater)

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

        activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                isDataFetched = true
            }
        }


        binding.ivHomeAtdaily.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            activityLauncher.launch(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        binding.ivProfileAtdaily.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            activityLauncher.launch(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.tvStartdate.setOnClickListener {
            showDatePickerDialog(true)
        }

        binding.tvEnddate.setOnClickListener {
            userHistories.clear()
            showDatePickerDialog(false)
        }

        binding.ivRefresh.setOnClickListener{
            runOnUiThread {
                recreate()
            }

            //refreshActivity()
        }
    }

    private fun getData(){

        val userPreferences = UserPreferences.getInstance(dataStore)
        val userSessionFlow = userPreferences.getUserSession()

        lifecycleScope.launch{
            userSessionFlow.collect { userSession ->
                val sessionToken = userSession.token
                if (!sessionToken.isNullOrEmpty() && !isDataFetched) {
                    Log.d("selected date", selectedStartDate ?: "")
                    Log.d("selected date end", selectedEndDate ?: "")

                    viewModel.getUserHistory(sessionToken, selectedStartDate ?: "", selectedEndDate ?: "").observe(this@ScheduleActivity){ result ->
                        when(result){

                            is Resource.Loading -> {

                            }

                            is Resource.Success -> {



                                val userHistoryFlow = userPreferences.getUserHistory()

                                lifecycleScope.launch{
                                    userHistoryFlow.collect{

                                        var totalCalories = 0.00
                                        var totalTime = 0.00

                                        for(data in userHistories){
                                            if(data.historyId != null){
                                                val calories = data.calories ?: 0.00
                                                totalCalories += calories
                                                val time = data.time ?: 0.00
                                                totalTime += time
                                                val timeToSec = totalTime.div(60)
                                                val type = data.type

                                                binding.tvCalories.text = totalCalories.toString()
                                                binding.tvTime.text = timeToSec.toString()

                                                if(type == "pushup"){
                                                    binding.ivPushup.alpha += 0.2F
                                                } else {
                                                    binding.ivSitup.alpha += 0.2F
                                                }
                                            }
                                        }

                                        if (userHistories.isEmpty()) {
                                            binding.tvCalories.text = "0"
                                            binding.tvTime.text = "0"
                                            binding.ivPushup.alpha = 0F
                                            binding.ivSitup.alpha = 0F
                                        }
                                    }
                                }
                            }
                            is Resource.Error -> {
                                Log.d("Error", result.message ?: "")
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePickerDialog(isStartDate: Boolean) {

        val c = Calendar.getInstance()

        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
            val formattedDate = formatDate(
                Calendar.getInstance().apply {
                    set(year, monthOfYear, dayOfMonth)
                }.timeInMillis
            )
            if (isStartDate) {
                selectedStartDate = formattedDate
                binding.tvStartdate.text = formattedDate
            } else {
                selectedEndDate = formattedDate
                binding.tvEnddate.text = formattedDate
            }

            if (selectedStartDate != null && selectedEndDate != null) {
                getData()
            }
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun formatDate(date: Long): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(date))
    }

    private fun refreshActivity() {
        recreate()
    }

}