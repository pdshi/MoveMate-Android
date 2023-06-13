package com.stevennt.movemate.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.stevennt.movemate.R
import com.stevennt.movemate.databinding.ActivityProfileBinding
import com.stevennt.movemate.preference.UserPreferences
import com.stevennt.movemate.ui.auth.LoginActivity
import com.stevennt.movemate.ui.schedule.ScheduleActivity
import com.stevennt.movemate.ui.home.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Suppress("DEPRECATION")
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var coroutineScope: CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(binding.root)
        supportActionBar?.hide()

        binding.ivHomeAtprofile.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        binding.ivDailyAtprofile.setOnClickListener{
            val intent = Intent(this, ScheduleActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        auth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

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
                    binding.tvNameProfile.text = userData.displayName
                }
            }
        }

        binding.logoutBtn.setOnClickListener{
            signOut()
        }
    }

    private fun signOut() {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener(this) {
            // Clear user session in DataStore
            clearUserSession()

            // Redirect to Login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun clearUserSession() {
        coroutineScope.launch {
            val userPreferences = UserPreferences.getInstance(dataStore)
            userPreferences.destroyUserSession()
        }
    }
}