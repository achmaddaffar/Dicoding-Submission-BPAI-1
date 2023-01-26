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
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.auth.UserModel
import com.example.storyapp.data.auth.UserPreference
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.ui.main.ListStoryActivity
import com.example.storyapp.ui.register.RegisterActivity
import com.example.storyapp.utils.Helper.Companion.dataStore
import com.example.storyapp.utils.Helper.Companion.isValidEmail
import com.example.storyapp.utils.Helper.Companion.isValidPassword
import com.example.storyapp.utils.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var user: UserModel
    private lateinit var dialog: Dialog

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
            ViewModelFactory(UserPreference.getInstance(dataStore), application)
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
                    isLoginInvalid(false)
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
                    isLoginInvalid(false)
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
                    if (email != user.email || password != user.password) {
                        isLoginInvalid(true)
                    } else {
//                        viewModel.login()
                        val intent = Intent(this@LoginActivity, ListStoryActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            }

            tvRegister.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
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

    private fun isLoginInvalid(isError: Boolean) {
        binding.cvLoginInvalid.visibility = if (isError) View.VISIBLE else View.GONE
    }
}