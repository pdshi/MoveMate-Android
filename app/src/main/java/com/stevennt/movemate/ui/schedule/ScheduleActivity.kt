package com.stevennt.movemate.ui.schedule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Spinner
import androidx.activity.viewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.stevennt.movemate.databinding.ActivityScheduleBinding
import com.stevennt.movemate.ui.ViewModelFactory
import com.stevennt.movemate.ui.home.HomeActivity
import com.stevennt.movemate.ui.profile.ProfileActivity
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION", "KotlinConstantConditions")
class ScheduleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScheduleBinding
    private val viewModel: ScheduleViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private var selectedStartDate: Long? = null
    private var selectedEndDate: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduleBinding.inflate(layoutInflater)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(binding.root)
        supportActionBar?.hide()

        binding.ivHomeAtdaily.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        binding.ivProfileAtdaily.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.editTextStartDate.setOnClickListener{
            showDatePickerDialog(true)
        }

        binding.editTextEndDate.setOnClickListener{
            showDatePickerDialog(false)
        }
    }

    private fun showDatePickerDialog(isStartDate: Boolean) {
        val builder = MaterialDatePicker.Builder.dateRangePicker()

        val constraintsBuilder = CalendarConstraints.Builder()
        constraintsBuilder.setValidator(DateValidatorPointForward.now())

        builder.setCalendarConstraints(constraintsBuilder.build())

        val picker = builder.build()

        picker.addOnPositiveButtonClickListener { selection ->
            val dateRange = selection as? Pair<*, *>?
            if (dateRange != null) {
                val startDate = dateRange.first as? Long
                val endDate = dateRange.second as? Long

                Log.d("DatePicker", "Selected start date: $startDate")
                Log.d("DatePicker", "Selected end date: $endDate")

                if (isStartDate) {
                    selectedStartDate = startDate
                    binding.editTextStartDate.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(startDate!!)))
                } else {
                    selectedEndDate = endDate
                    binding.editTextEndDate.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(endDate!!)))
                }
            }
        }
        picker.show(supportFragmentManager, picker.toString())
    }
}