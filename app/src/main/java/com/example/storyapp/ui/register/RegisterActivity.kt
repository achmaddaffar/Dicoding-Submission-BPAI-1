package com.example.storyapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.auth.UserModel
import com.example.storyapp.data.auth.UserPreference
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.ui.login.LoginActivity
import com.example.storyapp.utils.Helper.Companion.dataStore
import com.example.storyapp.utils.Helper.Companion.isValidEmail
import com.example.storyapp.utils.Helper.Companion.isValidPassword
import com.example.storyapp.utils.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupViewModel()
        setButtonEnable()
        setupAction()
        playAnimation()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[RegisterViewModel::class.java]
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
        binding.apply {
            edRegisterName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
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
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButtonEnable()
                }

                override fun afterTextChanged(s: Editable?) {
                    val errorText =
                        if (!edRegisterPassword.text.toString()
                                .isValidPassword()
                        ) getString(R.string.longer_than_8_chars)
                        else null
                    edRegisterPassword.setError(errorText, null)
                }
            })

            edRegisterConfirmPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
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
                    viewModel.saveUser(UserModel(name, email, password, true))
                }
                AlertDialog.Builder(this@RegisterActivity).apply {
                    setTitle("Registered")
                    setMessage("Your account has been created successfully")
                    setPositiveButton("Sign in") { _, _ ->
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                    create()
                    show()
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
        val tvName = ObjectAnimator.ofFloat(binding.tvName, View.ALPHA, 1f).setDuration(500)
        val tilName = ObjectAnimator.ofFloat(binding.tilName, View.ALPHA, 1f).setDuration(500)
        val tvEmail = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val tilEmail = ObjectAnimator.ofFloat(binding.tilEmail, View.ALPHA, 1f).setDuration(500)
        val tvPassword = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(500)
        val tilPassword =
            ObjectAnimator.ofFloat(binding.tilPassword, View.ALPHA, 1f).setDuration(500)
        val tvConfirmPassword =
            ObjectAnimator.ofFloat(binding.tvConfirmPassword, View.ALPHA, 1f).setDuration(500)
        val tilConfirmPassword =
            ObjectAnimator.ofFloat(binding.tilConfirmPassword, View.ALPHA, 1f).setDuration(500)
        val btnRegister =
            ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)
        val llLogin = ObjectAnimator.ofFloat(binding.llLogin, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                greeting,
                tvName,
                tilName,
                tvEmail,
                tilEmail,
                tvPassword,
                tilPassword,
                tvConfirmPassword,
                tilConfirmPassword,
                btnRegister,
                llLogin
            )
            start()
        }
    }
}