package com.example.storyapp.ui.splash

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.local.UserModel
import com.example.storyapp.data.local.UserPreference
import com.example.storyapp.databinding.ActivitySplashScreenBinding
import com.example.storyapp.ui.login.LoginActivity
import com.example.storyapp.ui.main.ListStoryActivity
import com.example.storyapp.utils.Helper.Companion.dataStore
import com.example.storyapp.utils.ViewModelFactory

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var viewModel: SplashScreenViewModel
    private var isLogin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        playAnimation()
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this, ViewModelFactory(UserPreference.getInstance(dataStore), application)
        )[SplashScreenViewModel::class.java]

        viewModel.apply {
            getUser().observe(this@SplashScreenActivity) { user ->
                this@SplashScreenActivity.isLogin = user.token.isNotEmpty()
            }

            getTheme().observe(this@SplashScreenActivity) { isDarkModeActive ->
                if (isDarkModeActive)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun playAnimation() = AnimatorSet().apply {
        playSequentially(
            ObjectAnimator.ofFloat(binding.ivSplashlogo, View.ALPHA, 1f).setDuration(2000),
            ObjectAnimator.ofFloat(binding.ivSplashlogo, View.TRANSLATION_Y, 200f).setDuration(800),
            ObjectAnimator.ofFloat(binding.ivSplashlogo, View.TRANSLATION_Y, -10000f)
                .setDuration(600)
        )
        start()
    }.addListener(object : AnimatorListener {
        override fun onAnimationStart(a: Animator) {}

        override fun onAnimationEnd(a: Animator) {
            val intent =
                if (isLogin) {
                    Intent(this@SplashScreenActivity, ListStoryActivity::class.java)
                } else {
                    Intent(this@SplashScreenActivity, LoginActivity::class.java)
                }
            startActivity(intent)
            finish()
        }

        override fun onAnimationCancel(a: Animator) {}

        override fun onAnimationRepeat(a: Animator) {}
    })
}