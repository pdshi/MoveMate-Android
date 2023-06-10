package com.stevennt.movemate.ui.welcome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.lifecycleScope
import com.stevennt.movemate.R
import com.stevennt.movemate.databinding.ActivityProfileBinding
import com.stevennt.movemate.databinding.ActivityWelcomeBinding
import com.stevennt.movemate.preference.UserPreferences
import com.stevennt.movemate.ui.home.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Suppress("DEPRECATION")
class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var coroutineScope: CoroutineScope

    private val delayMillis: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)

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

        coroutineScope = CoroutineScope(Dispatchers.Main)

        val userPreferences = UserPreferences.getInstance(dataStore)
        val userDataFlow = userPreferences.getUserData()

        lifecycleScope.launch {
            userDataFlow.collect { userData ->
                if (!userData.userId.isNullOrEmpty()) {
                    binding.tvWelcome.text = userData.displayName
                }
            }
        }

        Handler().postDelayed({
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }, delayMillis)
    }
}