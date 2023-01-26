package com.example.storyapp.ui.splash_screen

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
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.auth.UserModel
import com.example.storyapp.data.auth.UserPreference
import com.example.storyapp.databinding.ActivitySplashScreenBinding
import com.example.storyapp.ui.login.LoginActivity
import com.example.storyapp.ui.main.ListStoryActivity
import com.example.storyapp.utils.Helper.Companion.dataStore
import com.example.storyapp.utils.ViewModelFactory

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var viewModel: SplashScreenViewModel
    private lateinit var user: UserModel
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

        viewModel.getUser().observe(this) { user ->
            this.user = user
            this.isLogin = user.isLogin
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
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }

        override fun onAnimationCancel(a: Animator) {}

        override fun onAnimationRepeat(a: Animator) {}
    })
}