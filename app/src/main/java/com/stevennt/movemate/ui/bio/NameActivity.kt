package com.stevennt.movemate.ui.bio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.lifecycleScope
import com.stevennt.movemate.R
import com.stevennt.movemate.data.Resource
import com.stevennt.movemate.data.model.userDataArray
import com.stevennt.movemate.data.network.response.InputUserDataResp
import com.stevennt.movemate.data.network.response.LoginResp
import com.stevennt.movemate.databinding.ActivityNameBinding
import com.stevennt.movemate.databinding.ActivityScheduleBinding
import com.stevennt.movemate.preference.UserPreferences
import com.stevennt.movemate.preference.UserPreferences.Companion.InputUserData
import com.stevennt.movemate.ui.ViewModelFactory
import com.stevennt.movemate.ui.schedule.ScheduleViewModel
import kotlinx.coroutines.launch
import java.io.File

@Suppress("DEPRECATION")
class NameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNameBinding.inflate(layoutInflater)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(binding.root)
        supportActionBar?.hide()

        binding.backName.setOnClickListener{
            onBackPressed()
        }

        binding.nameButton.setOnClickListener{
            val name = binding.etName.text.toString()
            if (name.isNullOrEmpty()) {
                Toast.makeText(this@NameActivity, R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
            } else {
                userDataArray[0].displayName = name
                val intent = Intent(this, AgeActivity::class.java)
                startActivity(intent)
            }
        }

    }
}