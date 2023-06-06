package com.stevennt.movemate.ui.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.stevennt.movemate.R
import com.stevennt.movemate.databinding.ActivityDailyBinding
import com.stevennt.movemate.databinding.ActivityProfileBinding
import com.stevennt.movemate.preference.UserPreferences
import com.stevennt.movemate.ui.auth.LoginActivity
import com.stevennt.movemate.ui.daily.DailyActivity
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
        }
        binding.ivDailyAtprofile.setOnClickListener{
            val intent = Intent(this, DailyActivity::class.java)
            startActivity(intent)
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