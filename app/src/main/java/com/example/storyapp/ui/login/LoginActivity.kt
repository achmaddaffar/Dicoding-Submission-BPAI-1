package com.example.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.auth.UserModel
import com.example.storyapp.data.auth.UserPreference
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.utils.Helper.Companion.dataStore
import com.example.storyapp.utils.Helper.Companion.isValidEmail
import com.example.storyapp.utils.Helper.Companion.isValidPassword
import com.example.storyapp.utils.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var user: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupViewModel()
        setButtonEnable()
        setupAction()
        setupAnimation()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]

        viewModel.getUser().observe(this) { user ->
            this.user = user
        }
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
        binding.apply {
            edLoginEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButtonEnable()
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
                viewModel.login()
            }
        }
    }

    private fun setupAnimation() {
        val tvGreeting = ObjectAnimator.ofFloat(binding.tvGreeting, View.ALPHA, 1f).setDuration(500)
        val tvEmail = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val tilEmail = ObjectAnimator.ofFloat(binding.tilEmail, View.ALPHA, 1f).setDuration(500)
        val tvPassword = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(500)
        val tilPassword =
            ObjectAnimator.ofFloat(binding.tilPassword, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val llRegister = ObjectAnimator.ofFloat(binding.llRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                tvGreeting,
                tvEmail,
                tilEmail,
                tvPassword,
                tilPassword,
                btnLogin,
                llRegister
            )
            start()
        }
    }
}