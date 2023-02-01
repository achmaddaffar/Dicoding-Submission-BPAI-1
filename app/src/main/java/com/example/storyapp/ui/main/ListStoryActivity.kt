package com.example.storyapp.ui.main

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.data.local.UserPreference
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.databinding.ActivityListStoryBinding
import com.example.storyapp.ui.addStory.AddStoryActivity
import com.example.storyapp.ui.detailStory.DetailStoryActivity
import com.example.storyapp.ui.settings.SettingsActivity
import com.example.storyapp.utils.Helper.Companion.dataStore
import com.example.storyapp.utils.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlin.system.exitProcess

class ListStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListStoryBinding
    private lateinit var viewModel: ListStoryViewModel
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupAction()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        AlertDialog.Builder(this).apply {
            setMessage(getString(R.string.are_you_sure))
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                finish()
                exitProcess(0)
            }
            setNegativeButton(getString(R.string.no), null)
        }.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(
                    intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), application)
        )[ListStoryViewModel::class.java]

        viewModel.apply {
            isLoading.observe(this@ListStoryActivity) { isLoading ->
                showLoading(isLoading)
            }

            snackBarText.observe(this@ListStoryActivity) {
                it.getContentIfNotHandled()?.let { snackBarText ->
                    Snackbar.make(
                        window.decorView.rootView,
                        snackBarText,
                        Snackbar.LENGTH_SHORT
                    )
                        .setBackgroundTint(
                            ContextCompat.getColor(
                                this@ListStoryActivity,
                                R.color.red_light
                            )
                        )
                        .setTextColor(
                            ContextCompat.getColor(
                                this@ListStoryActivity,
                                R.color.black
                            )
                        )
                        .show()
                }
            }

            listStory.observe(this@ListStoryActivity) { listStory ->
                setStoryList(listStory)
            }

            getUser().observe(this@ListStoryActivity) { user ->
                viewModel.getAllStory("Bearer ${user.token}")
            }
        }
    }

    private fun setStoryList(listStory: List<ListStoryItem>) {
        val adapter = ListStoryAdapter(listStory)
        adapter.setOnItemClickCallback(object : ListStoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem) {
                val intent = Intent(this@ListStoryActivity, DetailStoryActivity::class.java)
                intent.putExtra(STORY_EXTRA, data)
                startActivity(
                    intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this@ListStoryActivity)
                        .toBundle()
                )
            }
        })
        binding.rvStoryList.adapter = adapter
    }

    private fun setupAction() {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_loading)
        dialog.setCancelable(false)
        if (dialog.window != null)
            dialog.window?.setBackgroundDrawable(ColorDrawable(0))

        val layoutManager = LinearLayoutManager(this)
        binding.apply {
            rvStoryList.layoutManager = layoutManager
            rvStoryList.setHasFixedSize(true)

            fabPost.setOnClickListener {
                val intent = Intent(this@ListStoryActivity, AddStoryActivity::class.java)
                startActivity(
                    intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this@ListStoryActivity)
                        .toBundle()
                )
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) dialog.show() else dialog.cancel()
    }

    companion object {
        const val STORY_EXTRA = "story"
    }
}