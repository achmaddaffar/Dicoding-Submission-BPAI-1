package com.example.storyapp.utils

import android.content.Context
import android.util.Patterns
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

class Helper {
    companion object {
        fun String.isValidPassword() = !isNullOrEmpty() && this.count() >= 8
        fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    }
}