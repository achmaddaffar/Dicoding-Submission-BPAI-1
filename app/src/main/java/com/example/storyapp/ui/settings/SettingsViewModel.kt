package com.example.storyapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.local.UserPreference
import kotlinx.coroutines.launch

class SettingsViewModel(private val pref: UserPreference) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            pref.deleteUser()
        }
    }
}