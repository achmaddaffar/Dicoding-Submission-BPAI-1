package com.example.storyapp.ui.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.local.UserPreference
import com.example.storyapp.databinding.ActivitySettingsBinding
import com.example.storyapp.ui.login.LoginActivity
import com.example.storyapp.utils.Helper.Companion.dataStore
import com.example.storyapp.utils.ViewModelFactory
import java.util.Locale

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), application)
        )[SettingsViewModel::class.java]

        viewModel.getThemeSetting().observe(this) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.switchTheme.isChecked = false
            }
        }
    }

    private fun setupAction() {
        binding.apply {
            btnLogout.setOnClickListener {
                AlertDialog.Builder(this@SettingsActivity).apply {
                    setTitle(getString(R.string.logout))
                    setMessage(getString(R.string.are_you_sure_logout))
                    setPositiveButton(getString(R.string.yes)) { _, _ ->
                        viewModel.logout()
                        val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    setNegativeButton(getString(R.string.no), null)
                }.show()
            }

            switchTheme.setOnCheckedChangeListener { _, isChecked ->
                viewModel.saveThemeSetting(isChecked)
            }

            btnChangeLanguage.text = Locale.getDefault().language
            btnChangeLanguage.setOnClickListener {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
        }
    }
}