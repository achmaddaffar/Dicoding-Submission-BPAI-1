package com.example.storyapp.ui.detailStory

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.ui.main.ListStoryActivity.Companion.STORY_EXTRA

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
    }

    private fun setupAction() {
        @Suppress("DEPRECATION") val item =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(STORY_EXTRA, ListStoryItem::class.java)
            } else {
                intent.getParcelableExtra<ListStoryItem>(STORY_EXTRA)
            }
        binding.apply {
            Glide.with(this@DetailStoryActivity)
                .load(item?.photoUrl)
                .into(ivDetailPhoto)

            tvDetailName.text = item?.name
            tvDetailDescription.text = item?.description
        }
    }
}