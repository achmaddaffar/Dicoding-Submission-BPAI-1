package com.example.storyapp.data.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

//    fun getUser(): Flow<UserModel> {
//        return dataStore.data.map { preferences ->
//            UserModel(
//                preferences[NAME_KEY] ?: "",
//                preferences[EMAIL_KEY] ?: "",
//                preferences[PASSWORD_KEY] ?: "",
//                preferences[STATE_KEY] ?: false
//            )
//        }
//    }

    suspend fun saveUser(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = user.name
            preferences[EMAIL_KEY] = user.email

            // APAKAH PASSWORD LAYAK DISIMPAN DI PREFERENCE ???
            // OoOOOooOOooOo
            
            preferences[PASSWORD_KEY] = user.password
            preferences[STATE_KEY] = user.isLogin
        }
    }

//    suspend fun login(token: String) {
//        dataStore.edit { preferences ->
//            preferences[STATE_KEY] = true
//            preferences[TOKEN_KEY] = token
//        }
//    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[STATE_KEY] = false
            preferences[TOKEN_KEY] = ""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val NAME_KEY = stringPreferencesKey("name")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val PASSWORD_KEY = stringPreferencesKey("password")
        private val STATE_KEY = booleanPreferencesKey("state")
        private val TOKEN_KEY = stringPreferencesKey("token")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}