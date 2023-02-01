package com.example.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.local.UserPreference
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.ui.main.ListStoryActivity
import com.example.storyapp.ui.register.RegisterActivity
import com.example.storyapp.utils.Helper.Companion.dataStore
import com.example.storyapp.utils.Helper.Companion.isValidEmail
import com.example.storyapp.utils.Helper.Companion.isValidPassword
import com.example.storyapp.utils.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupViewModel()
        setButtonEnable()
        setupAction()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), application)
        )[LoginViewModel::class.java]

        viewModel.apply {
            isAnimate.observe(this@LoginActivity) {
                if (it.hasBeenHandled)
                    showView()
                it.getContentIfNotHandled()?.let {
                    playAnimation()
                }
            }

            snackBarText.observe(this@LoginActivity) {
                it.getContentIfNotHandled()?.let { snackBarText ->
                    Snackbar.make(
                        window.decorView.rootView,
                        snackBarText,
                        Snackbar.LENGTH_SHORT
                    )
                        .setBackgroundTint(
                            ContextCompat.getColor(
                                this@LoginActivity,
                                R.color.red_light
                            )
                        )
                        .setTextColor(
                            ContextCompat.getColor(
                                this@LoginActivity,
                                R.color.black
                            )
                        )
                        .show()
                }
            }

            isLoading.observe(this@LoginActivity) { isLoading ->
                showLoading(isLoading)
            }

            loginError.observe(this@LoginActivity) { loginError ->
                showLoginInvalid(loginError)

                if(!loginError) {
                    val intent = Intent(this@LoginActivity, ListStoryActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun showView() {
        binding.apply {
            tvGreeting.alpha = 1f
            tvEmail.alpha = 1f
            tilEmail.alpha = 1f
            tvPassword.alpha = 1f
            tilPassword.alpha = 1f
            btnLogin.alpha = 1f
            llRegister.alpha = 1f
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading)
            dialog.show()
        else
            dialog.cancel()
    }

    private fun setButtonEnable() {
        val checkEmail = binding.edLoginEmail.text
        val checkPassword = binding.edLoginPassword.text

        binding.btnLogin.isEnabled =
            checkEmail != null && checkEmail.toString().isNotEmpty() && checkEmail.isValidEmail() &&
                    checkPassword != null && checkPassword.toString().isNotEmpty() &&
                    checkPassword.toString().isValidPassword()
    }

    private fun setupAction() {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_loading)
        dialog.setCancelable(false)
        if (dialog.window != null)
            dialog.window?.setBackgroundDrawable(ColorDrawable(0))

        binding.apply {
            edLoginEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButtonEnable()
                    showLoginInvalid(false)
                }

                override fun afterTextChanged(s: Editable?) {
                    edLoginEmail.error = if (edLoginEmail.text.toString()
                            .isEmpty()
                    ) getString(R.string.this_field_cannot_be_blank)
                    else if (!s.isValidEmail()) getString(R.string.email_is_invalid)
                    else null
                }
            })

            edLoginPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButtonEnable()
                    showLoginInvalid(false)
                }

                override fun afterTextChanged(s: Editable?) {
                    val errorText =
                        if (!edLoginPassword.text.toString()
                                .isValidPassword()
                        ) getString(R.string.longer_than_8_chars)
                        else null
                    edLoginPassword.setError(errorText, null)
                }
            })

            btnLogin.setOnClickListener {
                binding.let {
                    val email = it.edLoginEmail.text.toString()
                    val password = it.edLoginPassword.text.toString()
                    viewModel.login(email, password)
                }
            }

            tvRegister.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun playAnimation() {
        val greeting = ObjectAnimator.ofFloat(binding.tvGreeting, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val emailInput = ObjectAnimator.ofFloat(binding.tilEmail, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(500)
        val passwordInput =
            ObjectAnimator.ofFloat(binding.tilPassword, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.llRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                greeting,
                email,
                emailInput,
                password,
                passwordInput,
                login,
                register
            )
            start()
        }
    }

    private fun showLoginInvalid(isError: Boolean) {
        binding.cvLoginInvalid.visibility = if (isError) View.VISIBLE else View.GONE
    }
}