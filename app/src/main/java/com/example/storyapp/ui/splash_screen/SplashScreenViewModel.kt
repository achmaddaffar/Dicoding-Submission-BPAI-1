package com.example.storyapp.ui.splash_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.data.auth.UserModel
import com.example.storyapp.data.auth.UserPreference

class SplashScreenViewModel(private val pref : UserPreference): ViewModel() {
    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }
}