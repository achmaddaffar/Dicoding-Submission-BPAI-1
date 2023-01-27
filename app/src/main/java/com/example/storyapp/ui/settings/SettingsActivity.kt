package com.example.storyapp.ui.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.local.UserPreference
import com.example.storyapp.databinding.ActivitySettingsBinding
import com.example.storyapp.utils.Helper.Companion.dataStore
import com.example.storyapp.utils.ViewModelFactory

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
    }


    private fun setupAction() {
        binding.apply {
            btnLogout.setOnClickListener {
                AlertDialog.Builder(this@SettingsActivity).apply {
                    setTitle(getString(R.string.logout))
                    setMessage(getString(R.string.are_you_sure_logout))
                    setPositiveButton(getString(R.string.yes)) { _, _ ->
                        viewModel.logout()
                        finish()
                    }
                    setNegativeButton(getString(R.string.no), null)
                }.show()
            }
        }
    }
}