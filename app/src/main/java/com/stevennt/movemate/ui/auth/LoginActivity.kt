package com.stevennt.movemate.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.stevennt.movemate.R
import com.stevennt.movemate.data.Resource
import com.stevennt.movemate.databinding.ActivityLoginBinding
import com.stevennt.movemate.preference.UserPreferences
import com.stevennt.movemate.ui.ViewModelFactory
import com.stevennt.movemate.ui.bio.GenderActivity
import com.stevennt.movemate.ui.bio.NameActivity
import com.stevennt.movemate.ui.home.HomeActivity
import com.stevennt.movemate.ui.utils.CustomEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var activity: LoginActivity
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var coroutineScope: CoroutineScope
    private val viewModel: AuthViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(binding.root)
        supportActionBar?.hide()
        activity = this

        binding.tvForRegister.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = Firebase.auth

        dataStore = PreferenceDataStoreFactory.create(
            produceFile = {
                File(application.filesDir, "user_prefs.preferences_pb")
            }
        )
        coroutineScope = CoroutineScope(Dispatchers.Main)

        setUp()
    }

    private fun CustomEditText.listener() {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                with(binding) {
                    loginButton.isEnabled = etPasswordlogin.isValid && etEmaillogin.isValid
                }
            }

        })
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.login_button -> {
                val email = binding.etEmaillogin.text?.toString()
                val password = binding.etPasswordlogin.text?.toString()

                if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
                    Toast.makeText(this@LoginActivity, R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
                    return
                }

                viewModel.login(email, password).observe(this@LoginActivity) { result ->
                    if(result != null) {
                        when (result) {
                            is Resource.Loading -> {
                                showLoading(true)
                            }
                            is Resource.Success -> {
                                showLoading(false)

                                val userPreferences = UserPreferences.getInstance(dataStore)
                                val userSessionFlow = userPreferences.getUserSession()

                                lifecycleScope.launch {
                                    userSessionFlow.collect { userSession ->
                                        val token = userSession.token
                                        if (token != null) {
                                            if (token.isNotEmpty()) {
                                                Log.d(TAG, "Saved Token: $token")
                                                Intent(this@LoginActivity, HomeActivity::class.java).apply {
                                                    startActivity(this)
                                                    finish()
                                                }
                                            } else {
                                                // Token is not available yet, handle the case accordingly
                                            }
                                        }
                                    }
                                }

                            }
                            is Resource.Error -> {
                                Log.d(TAG, "Login Error: ${result.data?.token}")
                                showLoading(false)
                                Toast.makeText(this@LoginActivity, result.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
            R.id.login_with_google_button -> {
                signIn()
            }
        }
    }


    private fun setUp(){
        with(binding){
            loginButton.setOnClickListener(activity)
            etEmaillogin.listener()
            etPasswordlogin.listener()
            loginWithGoogleButton.setOnClickListener(activity)
        }
    }

    private fun showLoading(loadingState: Boolean) {
        with(binding.loginButton) {
            isEnabled = !loadingState
            text = context.getString(if (loadingState) R.string.loading else R.string.sign_in_string)
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

        private fun firebaseAuthWithGoogle(idToken: String) {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithCredential:success")
                        val user = auth.currentUser
                        val firebaseToken = user?.getIdToken(false)?.result?.token ?: ""
                        if (user != null) {
                            viewModel.loginWithFirebase(firebaseToken).observe(this@LoginActivity) { firebaseResult ->
                                when (firebaseResult) {
                                    is Resource.Loading -> {
                                        // Handle Firebase login loading state
                                    }
                                    is Resource.Success -> {
                                        // Firebase login successful
                                        if (isTokenLoggedIn(user.uid)) {
                                            startHomeActivity()
                                            finish()
                                        } else {
                                            //userPreferences.saveTokenLoginStatus(user.uid, false)
                                            startGenderActivity("")
                                            finish()
                                        }
                                    }
                                    is Resource.Error -> {
                                        // Firebase login error
                                        Toast.makeText(this@LoginActivity, "Firebase login error", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                    } else {
                        Log.d(TAG, "Firebase user is null")
                        updateUI(null)
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    /*private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && isTokenLoggedIn(idToken)) {
                        lifecycleScope.launch {
                            val userPreferences = UserPreferences.getInstance(dataStore)
                            val userSession = UserSession(idToken) // Use idToken as the user session token
                            userPreferences.saveUserSession(userSession)

                            viewModel.loginWithFirebase(idToken).observe(this@LoginActivity) { firebaseResult ->
                                when (firebaseResult) {
                                    is Resource.Loading -> {
                                        // Handle Firebase login loading state
                                    }
                                    is Resource.Success -> {
                                        // Firebase login successful
                                        startHomeActivity()
                                        finish()
                                    }
                                    is Resource.Error -> {
                                        // Firebase login error
                                        Toast.makeText(this@LoginActivity, "Firebase login error", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    } else {
                        val user = auth.currentUser
                        if (user != null) {
                            val userPreferences = UserPreferences.getInstance(dataStore)
                            lifecycleScope.launch {
                                userPreferences.saveTokenLoginStatus(idToken, false)
                            }
                        }
                        startGenderActivity(idToken)
                        finish()
                    }
                } else {
                    val user = auth.currentUser
                    if (user != null) {
                        val userPreferences = UserPreferences.getInstance(dataStore)
                        lifecycleScope.launch {
                            userPreferences.saveTokenLoginStatus(idToken, false)
                        }
                    }
                    startGenderActivity(idToken)
                    finish()
                }
            }
    }*/

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null){
            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
            finish()
        }
    }

    /*private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val userPreferences = UserPreferences.getInstance(dataStore)
            lifecycleScope.launch {
                val isLoggedIn = isTokenLoggedIn(currentUser.uid)
                if (isLoggedIn) {
                    val userSession = UserSession(currentUser.uid) // Use uid as the user session token
                    userPreferences.saveUserSession(userSession)

                    viewModel.loginWithFirebase(currentUser.uid).observe(this@LoginActivity) { firebaseResult ->
                        when (firebaseResult) {
                            is Resource.Loading -> {
                                // Handle Firebase login loading state
                            }
                            is Resource.Success -> {
                                // Firebase login successful
                                startHomeActivity()
                            }
                            is Resource.Error -> {
                                // Firebase login error
                                Toast.makeText(this@LoginActivity, "Firebase login error", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    userPreferences.saveTokenLoginStatus(currentUser.uid, false)
                    startGenderActivity("")
                }
                finish()
            }
        }
    }*/

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        /*val currentUser = auth.currentUser
        if (currentUser != null) {
            updateUI(currentUser)
        }*/
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun isTokenLoggedIn(token: String): Boolean {
        val userPreferences = UserPreferences.getInstance(dataStore)
        var isLoggedIn = false
        lifecycleScope.launch {
            val userSession = userPreferences.getUserSession().first()
            isLoggedIn = userSession.token!!.isNotEmpty() && userSession.token == token
        }
        return isLoggedIn
    }

    private fun startHomeActivity() {
        val intent = Intent(this, NameActivity::class.java)
        startActivity(intent)
    }

    private fun startGenderActivity(idToken: String) {
        val intent = Intent(this, GenderActivity::class.java)
        intent.putExtra("idToken", idToken)
        startActivity(intent)
    }

    companion object {
        private const val TAG = "LoginActivity"
    }

}