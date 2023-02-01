package com.example.storyapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.local.UserPreference
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.ui.login.LoginActivity
import com.example.storyapp.utils.Helper.Companion.dataStore
import com.example.storyapp.utils.Helper.Companion.isValidEmail
import com.example.storyapp.utils.Helper.Companion.isValidPassword
import com.example.storyapp.utils.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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
        )[RegisterViewModel::class.java]

        viewModel.apply {
            isLoading.observe(this@RegisterActivity) { isLoading ->
                showLoading(isLoading)
            }

            snackBarText.observe(this@RegisterActivity) {
                it.getContentIfNotHandled()?.let { snackBarText ->
                    Snackbar.make(
                        window.decorView.rootView,
                        snackBarText,
                        Snackbar.LENGTH_SHORT
                    )
                        .setBackgroundTint(
                            ContextCompat.getColor(
                                this@RegisterActivity,
                                R.color.red_light
                            )
                        )
                        .setTextColor(
                            ContextCompat.getColor(
                                this@RegisterActivity,
                                R.color.black
                            )
                        )
                        .show()
                }
            }

            isAnimate.observe(this@RegisterActivity) {
                if (it.hasBeenHandled)
                    showView()
                it.getContentIfNotHandled()?.let {
                    playAnimation()
                }
            }

            isUserCreated.observe(this@RegisterActivity) {
                if (it == true)
                    AlertDialog.Builder(this@RegisterActivity).apply {
                        setTitle(getString(R.string.registered))
                        setMessage(getString(R.string.account_created_successfully))
                        setCancelable(false)
                        setPositiveButton(getString(R.string.sign_in)) { _, _ ->
                            finish()
                        }
                        create()
                        show()
                    }
            }
        }
    }

    private fun setButtonEnable() {
        val checkName = binding.edRegisterName.text
        val checkEmail = binding.edRegisterEmail.text
        val checkPassword = binding.edRegisterPassword.text
        val checkConfirmPassword = binding.edRegisterConfirmPassword.text
        binding.btnRegister.isEnabled = checkName != null && checkName.toString().isNotEmpty() &&
                checkEmail != null && checkEmail.toString()
            .isNotEmpty() && checkEmail.isValidEmail() &&
                checkPassword != null && checkPassword.toString()
            .isNotEmpty() && checkPassword.toString().isValidPassword() &&
                checkConfirmPassword != null && checkConfirmPassword.toString()
            .isNotEmpty() && checkConfirmPassword.toString() == checkPassword.toString()
    }

    private fun setupAction() {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_loading)
        dialog.setCancelable(false)
        if (dialog.window != null)
            dialog.window?.setBackgroundDrawable(ColorDrawable(0))

        binding.apply {
            edRegisterName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                    edRegisterName.error = if (edRegisterName.text.toString()
                            .isEmpty()
                    ) getString(R.string.this_field_cannot_be_blank)
                    else null
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButtonEnable()
                }

                override fun afterTextChanged(s: Editable?) {
                    edRegisterName.error = if (edRegisterName.text.toString()
                            .isEmpty()
                    ) getString(R.string.this_field_cannot_be_blank)
                    else null
                }
            })

            edRegisterEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                    edRegisterEmail.error = if (edRegisterEmail.text.toString()
                            .isEmpty()
                    ) getString(R.string.this_field_cannot_be_blank)
                    else if (!s.isValidEmail()) getString(R.string.email_is_invalid)
                    else null
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButtonEnable()
                }

                override fun afterTextChanged(s: Editable?) {
                    edRegisterEmail.error = if (edRegisterEmail.text.toString()
                            .isEmpty()
                    ) getString(R.string.this_field_cannot_be_blank)
                    else if (!s.isValidEmail()) getString(R.string.email_is_invalid)
                    else null
                }
            })

            edRegisterPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                    val errorText =
                        if (!edRegisterPassword.text.toString()
                                .isValidPassword()
                        ) getString(R.string.longer_than_8_chars)
                        else if (edRegisterConfirmPassword.text.toString() != edRegisterPassword.text.toString()) getString(
                            R.string.password_match
                        )
                        else null
                    edRegisterPassword.setError(errorText, null)
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButtonEnable()
                }

                override fun afterTextChanged(s: Editable?) {
                    val errorText =
                        if (!edRegisterPassword.text.toString()
                                .isValidPassword()
                        ) getString(R.string.longer_than_8_chars)
                        else if (edRegisterConfirmPassword.text.toString() != edRegisterPassword.text.toString()) getString(
                            R.string.password_match
                        )
                        else null
                    edRegisterPassword.setError(errorText, null)
                }
            })

            edRegisterConfirmPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                    val errorText =
                        if (edRegisterConfirmPassword.text.toString() != edRegisterPassword.text.toString()) getString(
                            R.string.password_match
                        )
                        else null
                    edRegisterConfirmPassword.setError(errorText, null)
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButtonEnable()
                }

                override fun afterTextChanged(s: Editable?) {
                    val errorText =
                        if (edRegisterConfirmPassword.text.toString() != edRegisterPassword.text.toString()) getString(
                            R.string.password_match
                        )
                        else null
                    edRegisterConfirmPassword.setError(errorText, null)
                }
            })

            btnRegister.setOnClickListener {
                binding.let {
                    val name = it.edRegisterName.text.toString()
                    val email = it.edRegisterEmail.text.toString()
                    val password = it.edRegisterPassword.text.toString()
                    viewModel.registerUser(name, email, password)
                }
            }

            tvLogin.setOnClickListener {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun playAnimation() {
        val greeting = ObjectAnimator.ofFloat(binding.tvGreeting, View.ALPHA, 1f).setDuration(500)
        val name = ObjectAnimator.ofFloat(binding.tvName, View.ALPHA, 1f).setDuration(500)
        val nameInput = ObjectAnimator.ofFloat(binding.tilName, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val emailInput = ObjectAnimator.ofFloat(binding.tilEmail, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(500)
        val passwordInput =
            ObjectAnimator.ofFloat(binding.tilPassword, View.ALPHA, 1f).setDuration(500)
        val confirmPassword =
            ObjectAnimator.ofFloat(binding.tvConfirmPassword, View.ALPHA, 1f).setDuration(500)
        val confirmPasswordInput =
            ObjectAnimator.ofFloat(binding.tilConfirmPassword, View.ALPHA, 1f).setDuration(500)
        val register =
            ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.llLogin, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                greeting,
                name,
                nameInput,
                email,
                emailInput,
                password,
                passwordInput,
                confirmPassword,
                confirmPasswordInput,
                register,
                login
            )
        }.start()
    }

    private fun showView() {
        binding.apply {
            tvGreeting.alpha = 1f
            tvName.alpha = 1f
            tilName.alpha = 1f
            tvEmail.alpha = 1f
            tilEmail.alpha = 1f
            tvPassword.alpha = 1f
            tilPassword.alpha = 1f
            tvConfirmPassword.alpha = 1f
            tilConfirmPassword.alpha = 1f
            btnRegister.alpha = 1f
            llLogin.alpha = 1f
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) dialog.show() else dialog.cancel()
    }
}