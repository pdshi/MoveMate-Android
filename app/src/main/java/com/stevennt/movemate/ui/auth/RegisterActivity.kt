package com.stevennt.movemate.ui.auth

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.stevennt.movemate.MainActivity
import com.stevennt.movemate.R
import com.stevennt.movemate.data.Resource
import com.stevennt.movemate.databinding.ActivityRegisterBinding
import com.stevennt.movemate.ui.ViewModelFactory
import com.stevennt.movemate.ui.bio.GenderActivity
import com.stevennt.movemate.ui.home.HomeActivity
import com.stevennt.movemate.ui.utils.CustomEditText

@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var activity: RegisterActivity
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private val viewModel: AuthViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(binding.root)

        supportActionBar?.hide()
        activity = this

        binding.backRegis.setOnClickListener{
            onBackPressed()
        }

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize Firebase Auth
        auth = Firebase.auth

        setUp()

        binding.signupWithGoogleButton.setOnClickListener{
            signUp()
        }
    }

    private fun CustomEditText.listener() {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                with(binding) {
                    btnRegis.isEnabled = etEmailregis.isValid && etPasswordregis.isValid && etPasswordregis.text.toString() == etConfirmpassword.text.toString()
                }
            }

        })
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.btn_regis -> {
                val email = binding.etEmailregis.text?.toString()
                val password = binding.etPasswordregis.text?.toString()
                val confirmPassword = binding.etConfirmpassword.text?.toString()

                if (email.isNullOrEmpty() || password.isNullOrEmpty() || confirmPassword.isNullOrEmpty()) {
                    Toast.makeText(this@RegisterActivity, R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
                    return
                }

                if(password == confirmPassword){
                    viewModel.register(email, password, key = "supersecretapikey").observe(this@RegisterActivity) { result ->
                        if(result != null) {
                            when(result) {
                                is Resource.Loading -> {
                                    showLoading(true)
                                }
                                is Resource.Success -> {
                                    showLoading(false)
                                    Log.d(TAG, "Login Error: ${result.data}")
                                    Intent(this@RegisterActivity, GenderActivity::class.java).apply {
                                        startActivity(this)
                                        finish()
                                    }
                                }
                                is Resource.Error -> {
                                    showLoading(false)
                                    Toast.makeText(this@RegisterActivity, result.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                } else{
                    Toast.makeText(this, getString(R.string.password_not_match), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun signUp() {
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
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null){
            startActivity(Intent(this@RegisterActivity, HomeActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun setUp(){
        with(binding){
            btnRegis.setOnClickListener(activity)
            etEmailregis.listener()
            etPasswordregis.listener()
            etConfirmpassword.listener()
        }
    }

    private fun showLoading(loadingState: Boolean) {
        with(binding.btnRegis) {
            isEnabled = !loadingState
            text = context.getString(if (loadingState) R.string.loading else R.string.register_inactivity)
        }
    }

    companion object {
        private const val TAG = "RegisterActivity"
    }
}