package com.example.storyapp.ui.splash_screen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.databinding.ActivitySplashScreenBinding
import com.example.storyapp.ui.register.RegisterActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.apply {
            ivSplashlogo.alpha = 0f
            ivSplashlogo.animate()
                .setDuration(2000)
                .alpha(1f)
                .withEndAction {
                    val intent = Intent(this@SplashScreenActivity, RegisterActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                }
        }
    }
}