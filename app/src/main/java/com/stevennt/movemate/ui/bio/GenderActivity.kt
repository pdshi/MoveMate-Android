package com.stevennt.movemate.ui.bio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.stevennt.movemate.data.model.userDataArray
import com.stevennt.movemate.databinding.ActivityGenderBinding
import com.stevennt.movemate.ui.home.HomeActivity

@Suppress("DEPRECATION")
class GenderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGenderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenderBinding.inflate(layoutInflater)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(binding.root)
        supportActionBar?.hide()

        binding.ivMale.setOnClickListener{
            userDataArray[0].gender = "male"
            val intent = Intent(this, NameActivity::class.java)
            startActivity(intent)
        }

        binding.ivFemale.setOnClickListener{
            userDataArray[0].gender = "female"
            val intent = Intent(this, NameActivity::class.java)
            startActivity(intent)
        }
    }
}