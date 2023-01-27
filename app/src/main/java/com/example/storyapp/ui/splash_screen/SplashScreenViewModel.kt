package com.example.storyapp.ui.splash_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.data.local.UserModel
import com.example.storyapp.data.local.UserPreference

class SplashScreenViewModel(private val pref : UserPreference): ViewModel() {
    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }
}