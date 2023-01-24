package com.example.storyapp.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.auth.UserModel
import com.example.storyapp.data.auth.UserPreference
import kotlinx.coroutines.launch

class RegisterViewModel(private val pref: UserPreference) : ViewModel() {
    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }
}