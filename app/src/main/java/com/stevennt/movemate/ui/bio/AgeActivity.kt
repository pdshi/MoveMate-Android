package com.stevennt.movemate.ui.bio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.stevennt.movemate.R
import com.stevennt.movemate.data.model.userDataArray
import com.stevennt.movemate.databinding.ActivityAgeBinding

@Suppress("DEPRECATION")
class AgeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgeBinding.inflate(layoutInflater)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(binding.root)
        supportActionBar?.hide()

        binding.backAge.setOnClickListener{
            onBackPressed()
        }

        binding.btnAge.setOnClickListener{
            val age = binding.etAge.text.toString()
            val height = binding.etHeight.text.toString()
            val weight = binding.etWeight.text.toString()

            if (age.isNullOrEmpty() || height.isNullOrEmpty() || weight.isNullOrEmpty()) {
                Toast.makeText(this@AgeActivity, R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
            } else {
                userDataArray[0].age = age
                userDataArray[0].height = height
                userDataArray[0].weight = weight

                val intent = Intent(this, GoalActivity::class.java)
                startActivity(intent)
            }
        }
    }
}