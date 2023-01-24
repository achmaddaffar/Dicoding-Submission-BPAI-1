package com.example.storyapp.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityListStoryBinding

class ListStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}