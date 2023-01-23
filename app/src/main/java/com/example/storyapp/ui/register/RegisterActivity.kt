package com.example.storyapp.ui.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setButtonEnable()
        binding.apply {
            btnRegister.text = getString(R.string.register)
            edRegisterName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButtonEnable()
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            edRegisterEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButtonEnable()
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            edRegisterPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButtonEnable()
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            edRegisterConfirmPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButtonEnable()
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun setButtonEnable() {
        binding.btnRegister.text = getString(R.string.register)
        val checkName = binding.edRegisterName.text
        val checkEmail = binding.edRegisterEmail.text
        val checkPassword = binding.edRegisterPassword.text
        val checkConfirmPassword = binding.edRegisterConfirmPassword.text
        binding.btnRegister.isEnabled = checkName != null && checkName.toString().isNotEmpty() &&
                checkEmail != null && checkEmail.toString().isNotEmpty() &&
                checkPassword != null && checkPassword.toString().isNotEmpty() &&
                checkConfirmPassword != null && checkConfirmPassword.toString().isNotEmpty()
    }
}